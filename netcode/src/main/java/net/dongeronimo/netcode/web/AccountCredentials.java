package net.dongeronimo.netcode.web;
/**
 * REST login request json 
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
