package net.dongeronimo.netcode.service;

import org.springframework.stereotype.Service;

@Service
public class TimestampService {
    public boolean evaluateTimestamp(String timestamp) {
        //TO-DO, implementar. Timestamps sao unix time miliseconds, utc, não devem vir do futuro e nao devem vir de um
        //passado distante
        return true;
    }
}
