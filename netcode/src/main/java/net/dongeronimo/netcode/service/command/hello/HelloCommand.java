package net.dongeronimo.netcode.service.command.hello;

import java.util.ArrayList;
import java.util.HashMap;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.entities.InternetProperties;
import net.dongeronimo.netcode.entities.*;
import net.dongeronimo.netcode.entities.UserRepository;
import net.dongeronimo.netcode.service.OutboundDataBrokerService;
import net.dongeronimo.netcode.vo.IncomingPacketVO;
import net.dongeronimo.netcode.vo.OutboundDataVO;

@Service
public class HelloCommand {
    private final UserRepository userRepository;
    private final OutboundDataBrokerService outboundDataBroker;
    private final InternetPropertiesRepository internetPropertiesRepository;
    public HelloCommand(UserRepository _UserRepository,
    		OutboundDataBrokerService _outbouBrokerService, 
            InternetPropertiesRepository _InternetPropertiesRepository){
        userRepository = _UserRepository;
        outboundDataBroker = _outbouBrokerService;
        internetPropertiesRepository = _InternetPropertiesRepository;
    }
    /**
     * Tem que ter "HELLO" no rawPayload
     * @param packetVO
     * @return
     */
    public boolean handles(IncomingPacketVO packetVO) {
        return packetVO.rawPayload.trim().equals("HELLO");
    }
    @Transactional
    public void execute(IncomingPacketVO packetVO) {
        //Grava o ip e porta do cara
        User user = userRepository.findByUsername(packetVO.user.getUsername()).get();

        InternetProperties props = new InternetProperties();
        props.setIp(packetVO.address.getHostAddress());
        props.setPort(packetVO.port);
        props.setPlayer(user);
        props = internetPropertiesRepository.save(props);
        
        if(user.getInternetProperties() == null) 
            user.setInternetProperties(new ArrayList<>());
        user.getInternetProperties().add(props);
        user = userRepository.save(user);
        //it's not my job to answer the client. Let us shove the responsability on those that can.
        outboundDataBroker.push(new OutboundDataVO("connection_ack", user.getUsername(), new HashMap<>()));
    }
    
}
