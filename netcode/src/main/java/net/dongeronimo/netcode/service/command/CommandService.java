package net.dongeronimo.netcode.service.command;

import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.service.command.hello.HelloCommand;
import net.dongeronimo.netcode.vo.IncomingPacketVO;

@Service
public class CommandService {
    private final HelloCommand helloCommand;
    public CommandService(HelloCommand _HelloCommand){
        helloCommand = _HelloCommand;
    }
    public void handleCommand(IncomingPacketVO packetVO) {
        //pega o rawPayload e vê o que tem nele
        if(helloCommand.handles(packetVO)) {
            helloCommand.execute(packetVO);
        }
    }
    
}
