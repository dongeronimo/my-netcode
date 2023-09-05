package net.dongeronimo.netcode.service.command.hello;

import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.entities.InternetProperties;
import net.dongeronimo.netcode.entities.User;
import net.dongeronimo.netcode.entities.UserRepository;
import net.dongeronimo.netcode.vo.IncomingPacketVO;

@Service
public class HelloCommand {
    private final UserRepository userRepository;
    public HelloCommand(UserRepository _UserRepository){
        userRepository = _UserRepository;
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
        //Manda o pacote de resposta
    }
    
}
