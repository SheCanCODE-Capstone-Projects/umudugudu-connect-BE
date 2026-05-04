package com.umudugudu.service;

import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public AdminService() {
        userRepository = null;
    }

    @Transactional
    public String updateRoleByEmail(String email, Role newRole) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email));

        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "Role updated to " + newRole + " for " + email;
    }
}
