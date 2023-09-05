package net.dongeronimo.netcode.service.userManagement;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import net.dongeronimo.netcode.entities.User;
import net.dongeronimo.netcode.entities.UserRepository;
import net.dongeronimo.netcode.setup.JwtService;

@Component
public class SignInService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    public SignInService(UserRepository _userRepository, BCryptPasswordEncoder _pwdEncoder, 
     JwtService _JwtService, AuthenticationManager _authenticationManager){
        userRepository = _userRepository;
        passwordEncoder = _pwdEncoder;
        jwtService = _JwtService;
        authenticationManager = _authenticationManager;
    }

    public String createNewUser(String email, String username, String password){
        //Cria o novo usuário no db
        User newUser = new User(username, passwordEncoder.encode(password), email, "USER", null);
        newUser = userRepository.save(newUser);
        //Autentica o novo usuário pro jogador não precisar redigitar suas credenciais
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(
            newUser.getUsername(), newUser.getPassword());
        Authentication auth = authenticationManager.authenticate(creds);
        //Gera o token e o retorna
        String jwts = jwtService.getToken(auth.getName());
        return jwts;
    }
}
