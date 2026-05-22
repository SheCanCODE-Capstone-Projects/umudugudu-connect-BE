package com.umudugudu.service;

import com.umudugudu.dto.response.UserResponseDTO;
import com.umudugudu.entity.Role;

public interface AdminService {
    String updateRoleByEmail(String email, Role newRole);

    UserResponseDTO findUserByEmail(String email);
}
