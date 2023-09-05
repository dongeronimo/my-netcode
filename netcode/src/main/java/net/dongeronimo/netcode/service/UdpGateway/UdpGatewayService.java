package net.dongeronimo.netcode.service.UdpGateway;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.service.TimestampService;
import net.dongeronimo.netcode.service.command.CommandService;
import net.dongeronimo.netcode.setup.JwtService;
import net.dongeronimo.netcode.vo.IncomingPacketVO;
import net.dongeronimo.netcode.web.LoginController;

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
    /**
     * Token decoder
     */
    private JwtService jwtService; //TO-DO delete
    /**
     * To verifiy if the user actually exists
     */
    private UserDetailsService userDetailsService; //TO-DO delete
    /**
     * Timestamp validator, for now it only returns true.
     */
    private TimestampService timestampService; //TO-DO delete
        /**
     * Inicia o thread do server.
     * @param port a porta do udp
     */
    private InboundPacketProcessor packetProcessor;
    private CommandService commandService;
    public UdpGatewayService(@Value("${udp.port}") int port, 
                     @Value("${udp.datagramPacketSize}") int packetSize,
                     JwtService _JwtService,
                     UserDetailsService _userDetailsService,
                     TimestampService _TimestampService,
                     InboundPacketProcessor _InboundPacketProcessor,
                     CommandService _CommandService){
        this.jwtService = _JwtService;
        this.commandService = _CommandService;
        this.packetProcessor = _InboundPacketProcessor;
        this.userDetailsService = _userDetailsService;
        this.timestampService = _TimestampService;
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
