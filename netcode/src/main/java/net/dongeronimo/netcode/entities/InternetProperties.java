package net.dongeronimo.netcode.entities;

import javax.persistence.*;

@Entity
@Table(name="player_internet_properties")
public class InternetProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long id;
    @Column(nullable = false, unique = false)
    private String ip;
    @Column(nullable = false, unique = false)
    private int port;
    @ManyToOne
    @JoinColumn(name="player_id", nullable = false)
    private User player;

    public User getPlayer() {
        return this.player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }
    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
}
