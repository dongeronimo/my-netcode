package net.dongeronimo.netcode.service.command.hello;

import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.entities.InternetProperties;
import net.dongeronimo.netcode.entities.User;
import net.dongeronimo.netcode.entities.UserRepository;
import net.dongeronimo.netcode.service.OutboundDataBrokerService;
import net.dongeronimo.netcode.vo.IncomingPacketVO;
import net.dongeronimo.netcode.vo.OutboundDataVO;

@Service
public class HelloCommand {
    private final UserRepository userRepository;
    private final OutboundDataBrokerService outboundDataBroker;
    public HelloCommand(UserRepository _UserRepository,
    		OutboundDataBrokerService _outbouBrokerService){
        userRepository = _UserRepository;
        outboundDataBroker = _outbouBrokerService;
    }
    /**
     * Tem que ter "HELLO" no rawPayload
     * @param packetVO
     * @return
     */
    public boolean handles(IncomingPacketVO packetVO) {
        return packetVO.rawPayload.trim() == "HELLO";
    }

    public void execute(IncomingPacketVO packetVO) {
        //Grava o ip e porta do cara
        InternetProperties props = new InternetProperties();
        User user = userRepository.findByUsername(packetVO.user.getUsername()).get();
        props.setIp(packetVO.address.getHostAddress());
        props.setPort(packetVO.port);
        props.setPlayer(user);
        user.pushInternetProperties(props);
        userRepository.save(user);
        //it's not my job to answer the client. Let us shove the responsability on those that can.
        outboundDataBroker.push(new OutboundDataVO("connection_ack", user.getUsername(), null));
        //TO-DO, eu não devo fazer o push da msg aqui pq aqui eu n tenho o objeto de socket,
        //ele está no UdpGatewayService
        // //Manda o pacote de resposta
        // StringBuffer outboundData = new StringBuffer();
        // long unixTimestamp = System.currentTimeMillis();
        // outboundData.append(unixTimestamp);
        // outboundData.append("###");
        // outboundData.append("SERVER");
        // outboundData.append("###");
        // outboundData.append("Acknologed");
        // byte[] outboundBuffer = outboundData.toString().getBytes(StandardCharsets.US_ASCII);
        // DatagramPacket outboundPacket = new DatagramPacket(outboundBuffer, outboundBuffer.length, 
        //     packetVO.address, packetVO.port);
    }
    
}
