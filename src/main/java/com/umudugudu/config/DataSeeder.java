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

            if (!repo.existsByEmail("leader@gmail.com")) {
                User leader = new User();
                leader.setFirstName("Village");
                leader.setLastName("Leader");
                leader.setEmail("leader@gmail.com");
                leader.setPassword(encoder.encode("1234"));
                leader.setRole(Role.VILLAGE_LEADER);
                leader.setEnabled(true);
                leader.setVerified(true);
                repo.save(leader);
            }

            if (!repo.existsByEmail("citizen@gmail.com")) {
                User citizen = new User();
                citizen.setFirstName("Jane");
                citizen.setLastName("Citizen");
                citizen.setEmail("citizen@gmail.com");
                citizen.setPassword(encoder.encode("1234"));
                citizen.setRole(Role.CITIZEN);
                citizen.setPhoneNumber("+250788000001"); // replace with your real number to receive SMS
                citizen.setEnabled(true);
                citizen.setVerified(true);
                repo.save(citizen);
            }
        };
    }
}
