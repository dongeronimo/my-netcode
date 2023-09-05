package net.dongeronimo.netcode.udpServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

import javax.annotation.PreDestroy;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.time.Instant;

import net.dongeronimo.netcode.service.TimestampService;
import net.dongeronimo.netcode.service.user.UserDetailServiceImpl;
import net.dongeronimo.netcode.setup.JwtService;
import net.dongeronimo.netcode.web.LoginController;

@Component
public class UDPServer implements Runnable {
    Logger logger = LoggerFactory.getLogger(LoginController.class);
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
    private JwtService jwtService;
    /**
     * To verifiy if the user actually exists
     */
    private UserDetailsService userDetailsService;

    private TimestampService timestampService;
    /**
     * Inicia o thread do server.
     * @param port a porta do udp
     */
    public UDPServer(@Value("${udp.port}") int port, 
                     @Value("${udp.datagramPacketSize}") int packetSize,
                     JwtService _JwtService,
                     UserDetailsService _userDetailsService,
                     TimestampService _TimestampService){
        this.jwtService = _JwtService;
        this.userDetailsService = _userDetailsService;
        this.timestampService = _TimestampService;
        this.port = port;
        this.datagramPacketSize = packetSize;
        isRunning = true;
        udpServerThread= new Thread(this,"udp server");
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
    
    void processInboundPacket(String received) throws AccessDeniedException {
      String[] parts = received.split("###");
      if(parts.length != 3)
        throw new RuntimeException("invalid format:"+received);
      String timestamp = parts[0];
      if(timestampService.evaluateTimestamp(timestamp)==false){
        throw new RuntimeException("bad timestamp");
      }
      String token = parts[1];
      String username = jwtService.getAuthUser(token); //throws AccessDenied if token has no user.
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      String body = parts[2].trim();
    }
    /**
     * Loop do thread
     */
    @Override
    public void run() {
      // socket = createSocket(port);
      // byte[] buffer = new byte[datagramPacketSize];
      // while(isRunning){
      //   try{
      //     //Waits for a new Packet
      //     DatagramPacket inboundPacket = new DatagramPacket(buffer, buffer.length);
      //     socket.receive(inboundPacket); 
      //     //Reads the raw data as string
      //     InetAddress address = inboundPacket.getAddress();
      //     int port = inboundPacket.getPort();
      //     inboundPacket = new DatagramPacket(buffer, buffer.length, address, port);
      //     String received = new String(inboundPacket.getData(), 0, inboundPacket.getLength());
      //     //decodes the packet
      //     processInboundPacket(received);

      //     String[] parts = received.split("###");
      //     if(parts.length != 3)
      //       continue; //Está fora do padrão, não precisa nem continuar a sequência de operações
      //     //timestamp
      //     String timestamp = parts[0];
      //     //access control
      //     String token = parts[1];
      //     String username = jwtService.getAuthUser(token); //throws AccessDenied if token has no user.
      //     UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      //     //the body
      //     String body = parts[2].trim();
      //     System.out.println("timestamp="+timestamp+" user="+userDetails.getUsername()+" body="+body);
      //     //now we send something back. In the future we'll send some kind of world state
      //     StringBuffer outboundData = new StringBuffer();
      //     long unixTimestamp = System.currentTimeMillis();
      //     outboundData.append(unixTimestamp);
      //     outboundData.append("###");
      //     outboundData.append("TODO: Send World State");
      //     byte[] outboundBuffer = outboundData.toString().getBytes(StandardCharsets.US_ASCII);
      //     DatagramPacket outboundPacket = new DatagramPacket(outboundBuffer, outboundBuffer.length, inboundPacket.getAddress(), inboundPacket.getPort());
      //     socket.send(outboundPacket);
      //   }
      //   catch(Exception ex){
      //     logger.error("Eating exception in udp loop.", ex);
      //   }
      // }
    }
}
