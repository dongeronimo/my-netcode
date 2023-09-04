package net.dongeronimo.netcode.service.user;

import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;

import net.dongeronimo.netcode.entities.*;



@Configuration
public class UserDetailServiceImpl implements UserDetailsService{
    private final UserRepository userRepository;
    public UserDetailServiceImpl(UserRepository _userRepo){
        userRepository = _userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String u){
        Optional<User> usu = userRepository.findByUsername(u);
        UserBuilder builder = null;
        if(usu.isPresent()){
            User currentUser = usu.get();
            builder =  org.springframework.security.core.userdetails.User
                .withUsername(currentUser.getUsername());
            builder.password(currentUser.getPassword());
            builder.roles(currentUser.getRole());
        }else{
            throw new UsernameNotFoundException("User not found");
        }
        return builder.build();
    }
}
