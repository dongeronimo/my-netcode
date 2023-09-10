package net.dongeronimo.netcode.entities;
import java.util.List;

import javax.persistence.*;
/**
 * The user entity.
 */
@Entity
@Table(name="player_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @OneToMany(mappedBy = "player")
    private List<InternetProperties> internetProperties;



    public User(){}
    public User(String _username, String _password, String _email, 
                String _role, List<InternetProperties> lst) {
        username = _username;
        password = _password;
        role = _role;
        email = _email;  
        internetProperties = lst;
    }
    public List<InternetProperties> getInternetProperties() {
        return this.internetProperties;
    }

    public void setInternetProperties(List<InternetProperties> internetProperties) {
        this.internetProperties = internetProperties;
    }
    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
