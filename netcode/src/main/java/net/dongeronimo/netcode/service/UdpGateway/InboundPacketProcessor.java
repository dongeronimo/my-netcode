package net.dongeronimo.netcode.service.UdpGateway;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import net.dongeronimo.netcode.service.TimestampService;
import net.dongeronimo.netcode.setup.JwtService;
import net.dongeronimo.netcode.vo.IncomingPacketVO;


@Service
public class InboundPacketProcessor {
    Logger logger = LoggerFactory.getLogger(InboundPacketProcessor.class);
    private final TimestampService timestampService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    public InboundPacketProcessor(TimestampService _TimestampService,
                                  UserDetailsService _userDetailsService,
                                  JwtService _JwtService){
        this.timestampService = _TimestampService;
        this.jwtService = _JwtService;
        this.userDetailsService = _userDetailsService;
    }
    public IncomingPacketVO process(InetAddress address, int port, String received)throws RuntimeException{
        //is the number of pieces correct?
        String[] parts = received.split("###");
        if(parts.length != 3) {
          throw new RuntimeException("invalid format:"+received);
        }
        //is the timestamp valid?
        String timestamp = parts[0];
        if(timestampService.evaluateTimestamp(timestamp)==false){
          throw new RuntimeException("bad timestamp");
        }
        String token = parts[1];
        String username = jwtService.getAuthUser(token); //throws AccessDenied if token has no user.
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);        
        IncomingPacketVO packetVO = new IncomingPacketVO(address, port, userDetails, parts[2]);
        return packetVO;
    }
    
}
