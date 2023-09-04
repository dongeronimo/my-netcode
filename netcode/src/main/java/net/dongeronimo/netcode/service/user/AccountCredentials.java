package net.dongeronimo.netcode.service.user;
/**
 * REST login request json 
 * TODO: Put it together with the rest
 */
public class AccountCredentials {
    private String username;
    private String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String _username) {
        this.username = _username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String _password) {
        password = _password;
    }
}
