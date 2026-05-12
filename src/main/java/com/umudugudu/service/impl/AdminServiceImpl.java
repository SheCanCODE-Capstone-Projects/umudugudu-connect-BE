package com.umudugudu.service.impl;

import com.umudugudu.dto.response.UserResponseDTO;
import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.entity.Village;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.repository.VillageRepository;
import com.umudugudu.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VillageRepository villageRepository;

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

    @Override
    public UserResponseDTO findUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
    public String assignVillageLeader(String email, UUID villageId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new RuntimeException("Village not found with id: " + villageId));

        user.setRole(Role.VILLAGE_LEADER);
        user.setVillage(village);
        userRepository.save(user);

        return "User " + email + " assigned as village leader for village " + village.getName();
    }
}

