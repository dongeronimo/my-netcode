package net.dongeronimo.netcode.vo;

import java.net.InetAddress;

import org.springframework.security.core.userdetails.UserDetails;

public class IncomingPacketVO {
    public final InetAddress address;
    public final int port;
    public final UserDetails user;
    public final String rawPayload;
    public IncomingPacketVO(InetAddress _address, int _port, UserDetails _userDetails, String _rawPayload) {
        address = _address;
        port = _port;
        user = _userDetails;
        rawPayload = _rawPayload;
    }
}
