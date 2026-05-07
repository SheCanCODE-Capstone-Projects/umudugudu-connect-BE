package com.umudugudu.config;

import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            if (!repo.existsByEmail("admin@gmail.com")) {

                User admin = new User();
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("1234"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                admin.setVerified(true);

                repo.save(admin);
            }
        };
    }
}
