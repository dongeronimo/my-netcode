package net.dongeronimo.netcode.service.UdpGateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UdpGatewayServiceLauncher implements CommandLineRunner{
    private UdpGatewayService udpGateway;
    public UdpGatewayServiceLauncher(UdpGatewayService _udpGateway){
        udpGateway = _udpGateway;
    }
    @Override
    public void run(String... args) throws Exception {
        System.out.println(udpGateway);
    }
}
