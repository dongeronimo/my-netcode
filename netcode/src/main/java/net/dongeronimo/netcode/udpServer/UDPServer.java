package net.dongeronimo.netcode.udpServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.AccessDeniedException;

import javax.annotation.PreDestroy;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import net.dongeronimo.netcode.service.user.UserDetailServiceImpl;
import net.dongeronimo.netcode.setup.JwtService;

@Component
public class UDPServer implements Runnable {
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
    /**
     * Inicia o thread do server.
     * @param port a porta do udp
     */
    public UDPServer(@Value("${udp.port}") int port, 
                     @Value("${udp.datagramPacketSize}") int packetSize,
                     JwtService _JwtService,
                     UserDetailsService _userDetailsService ){
        this.jwtService = _JwtService;
        this.userDetailsService = _userDetailsService;
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
    
    /**
     * Loop do thread
     */
    @Override
    public void run() {
      socket = createSocket(port);
      byte[] buffer = new byte[datagramPacketSize];
      while(isRunning){
        try{
          //Waits for a new Packet
          DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
          socket.receive(packet); 
          //Reads the raw data as string
          InetAddress address = packet.getAddress();
          int port = packet.getPort();
          packet = new DatagramPacket(buffer, buffer.length, address, port);
          String received = new String(packet.getData(), 0, packet.getLength());
          //decodes the packet
          String[] parts = received.split(" body ");
          //access control
          String token = parts[0];
          String username = jwtService.getAuthUser(token); //throws AccessDenied if token has no user.
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          //the body
          String body = parts[1].trim();
          System.out.println("user = "+userDetails+" body = "+body);
        }
        catch(AccessDeniedException ex){
          
        }
        catch(UsernameNotFoundException ex){

        }
        catch(IOException ex){
          System.err.println("Erro de IO");
          ex.printStackTrace(System.err);
        }
      }
    }
}
