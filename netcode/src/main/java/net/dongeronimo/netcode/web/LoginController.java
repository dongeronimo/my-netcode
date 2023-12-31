package net.dongeronimo.netcode.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.dongeronimo.netcode.setup.JwtService;
/**
 * login endpoint: 
 *   POST /login
 *   body : {"username": USERNAME, "password": PASSWORD}
 *   The jwt is returned on the response header, at the Authorization value.
 */
@RestController
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    public LoginController(JwtService _JwtService,
        AuthenticationManager _authManager){
        jwtService = _JwtService;
        authenticationManager = _authManager;
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public ResponseEntity<?> getToken(@RequestBody 
        AccountCredentials credentials){
        UsernamePasswordAuthenticationToken creds = 
            new UsernamePasswordAuthenticationToken(
            credentials.getUsername(), credentials.getPassword());
        Authentication auth = authenticationManager.authenticate(creds);
        String jwts = jwtService.getToken(auth.getName());
        logger.info("User "+credentials.getUsername()+" signed in");
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwts)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
            .build();
        
    }
}
