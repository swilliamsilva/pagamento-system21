package com.pagamento.auth.bootstrap;

import com.pagamento.auth.model.User;
import com.pagamento.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, 
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("senha123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }
        };
    }
}