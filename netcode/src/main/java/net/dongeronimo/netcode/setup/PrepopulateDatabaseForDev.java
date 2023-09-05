package net.dongeronimo.netcode.setup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import net.dongeronimo.netcode.entities.User;
import net.dongeronimo.netcode.entities.UserRepository;

@Component
public class PrepopulateDatabaseForDev implements CommandLineRunner {
    public final UserRepository userRepository;
    public final BCryptPasswordEncoder passwordEncoder;

    public PrepopulateDatabaseForDev(UserRepository _userRepository, BCryptPasswordEncoder _encoder){
        userRepository = _userRepository;
        passwordEncoder = _encoder;
    }
    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        //String _username, String _password, String _role
        User alice = new User("alice", passwordEncoder.encode("blablabla"),"luciano.geronimo.fnord@gmail.com", 
            "USER", null);
        User bob = new User("bob",passwordEncoder.encode("blablabla"),"luciano.geronimo.lisboa@outlook.com","USER", null);
        userRepository.save(alice);
        userRepository.save(bob);
        System.out.println("Users de teste criados");
    }
    
}
