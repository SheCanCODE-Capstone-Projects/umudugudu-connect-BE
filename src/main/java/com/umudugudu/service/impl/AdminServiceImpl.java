package com.umudugudu.service.impl;

import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends AdminService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public String updateRoleByEmail(String email, Role newRole) {

        if (email == null || email.isEmpty()) {
            return "Email is required";
        }

        if (newRole == null) {
            return "Role is required";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole().equals(newRole)) {
            return "User already has role: " + user.getRole();
        }
        user.setRole(newRole);
        userRepository.save(user);
        return "Role updated to " + newRole + " for user " + email;
    }
}

