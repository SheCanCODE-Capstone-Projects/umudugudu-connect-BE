package com.umudugudu.service;

import com.umudugudu.dto.response.UserResponseDTO;
import com.umudugudu.entity.Role;

import java.util.UUID;

public interface AdminService {
    String updateRoleByEmail(String email, Role newRole);

    UserResponseDTO findUserByEmail(String email);

    String assignVillageLeader(String email, UUID villageId);
}
