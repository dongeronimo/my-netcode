package net.dongeronimo.netcode.service.UdpGateway;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.entities.InternetProperties;
import net.dongeronimo.netcode.entities.User;
import net.dongeronimo.netcode.entities.UserRepository;
import net.dongeronimo.netcode.service.OutboundDataBrokerService;
import net.dongeronimo.netcode.service.command.CommandService;
import net.dongeronimo.netcode.vo.IncomingPacketVO;
import net.dongeronimo.netcode.vo.OutboundDataVO;

@Service
public class UdpGatewayService implements Runnable {
    Logger logger = LoggerFactory.getLogger(UdpGatewayService.class);
    private int port;
    private int datagramPacketSize;
    /**
     * O socket é inicializado logo no começo do thread.
     */
    private DatagramSocket socket;
    /**
     * Thread do server UDP, iniciado no construtor, morto no destroy.
     */
    private Thread udpServerThread;
    /**
     * Flag de controle do loop infinito do thread do server udp. Se false pára o loop.
     */
    private boolean isRunning;
    @Autowired
    private UserRepository userRepository;
    private final OutboundDataBrokerService outboundDataBrokerService;
        /**
     * Inicia o thread do server.
     * @param port a porta do udp
     */
    private InboundPacketProcessor packetProcessor;
    private CommandService commandService;
    public UdpGatewayService(@Value("${udp.port}") int port, 
                     OutboundDataBrokerService _OutboundDataBrokerService,
                     @Value("${udp.datagramPacketSize}") int packetSize,
                     InboundPacketProcessor _InboundPacketProcessor,
                     CommandService _CommandService){
        this.commandService = _CommandService;
        this.outboundDataBrokerService = _OutboundDataBrokerService;
        outboundDataBrokerService.observer.subscribe((OutboundDataVO t)->{
        	try {
            logger.debug("Vai processar "+t.id+" "+t.toWhom+" "+t.what);
        		//monta o packet
        		StringBuffer outboundData = new StringBuffer();
        		long unixTimestamp = System.currentTimeMillis();
        		outboundData.append(unixTimestamp);
        		outboundData.append("###");
        		outboundData.append("SERVER");
        		outboundData.append("###");
        		outboundData.append(t.what);
            if(t.payload != null)
        		t.payload.keySet().forEach((String k)->{
        			outboundData.append(k);
        			outboundData.append(":");
        			outboundData.append(t.payload.get(k));
        		});
        		//vê pra quem mandar
        		List<String> destinations = Arrays.asList( t.toWhom.split(";") );
        		destinations.forEach( (String d)->{
        			try {
        				//envia
        				User dest = userRepository.findByUsername(d).get();//TODO: não enviar pra quem está offline
        				Comparator<InternetProperties> comp = (InternetProperties a, InternetProperties b)->{
        					return (int)(b.getId() - a.getId());//TODO: remover os end velhos
        				};
        				dest.getInternetProperties().sort(comp);
        				InternetProperties ipPort = dest.getInternetProperties().get(dest.getInternetProperties().size()-1);
        				byte[] outboundBuffer = outboundData.toString().getBytes();
        				//TODO: Barrar buffers co mais de 1400 bytes
        				DatagramPacket packet = new DatagramPacket(outboundBuffer, outboundBuffer.length, InetAddress.getByName(ipPort.getIp()), ipPort.getPort());
        				socket.send(packet);
        			}
        			catch(Exception ex) {
        				throw new RuntimeException(ex);
        			}
        		});
        	}
        	catch(Exception ex) {
        		logger.error(ex.getLocalizedMessage());
        	}
        }, (Throwable t)->{
        	
        });
        this.packetProcessor = _InboundPacketProcessor;
        this.port = port;
        this.datagramPacketSize = packetSize;
        isRunning = true;
        udpServerThread = new Thread(this,"udp server");
        udpServerThread.start();
    }
    /** Onde vou liberar os recursos. Liberar a porta udp entre outras coisas. Isso é necessário
     * pq senão quando eu alterar codigo e o hotreload rodar o servidor vai ficar travando a porta
     * se eu n matá-lo aquo
     * @throws InterruptedException
    */
    @PreDestroy
    public void destroy() throws InterruptedException {
        socket.close();
        isRunning = false;
        udpServerThread.join();
    }
    /**
     * Loop do thread
     */
    @Override
    public void run() {
        socket = createSocket(port);
        byte[] buffer = new byte[datagramPacketSize];
        while(isRunning){
            try{
              DatagramPacket inboundPacket = new DatagramPacket(buffer, buffer.length);
              socket.receive(inboundPacket); 
              //Reads the raw data as string
              InetAddress address = inboundPacket.getAddress();
              int port = inboundPacket.getPort();
              inboundPacket = new DatagramPacket(buffer, buffer.length, address, port);
              String received = new String(inboundPacket.getData(), 0, inboundPacket.getLength());
              IncomingPacketVO packetVO = packetProcessor.process(address, port, received);  
              commandService.handleCommand(packetVO);
            }
            catch(Exception ex){
            logger.error("Eating exception in udp loop.", ex);
            }
            
        }
    }

    private DatagramSocket createSocket(int port) {
        try{
          DatagramSocket socket = new DatagramSocket(port);
          return socket;
        }
        catch(Exception ex){
          //Se deu algum caô ao pegar o socket não há como nem pq continuar, joga a runtime exception
          throw new RuntimeException(ex);
        } 
      }
}
