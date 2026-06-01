package com.umudugudu.service;

import com.umudugudu.dto.response.AuditLogResponse;
import com.umudugudu.dto.response.DashboardResponse;
import com.umudugudu.dto.response.UserResponseDTO;
import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {


    String updateRoleByEmail(String email, Role newRole);
    UserResponseDTO findUserByEmail(String email);
    String assignVillageLeader(String email, UUID villageId);
    String createVillage(String name);


    DashboardResponse getDashboardStats();


    Page<UserResponseDTO> getAllUsers(String village, String role, int page, int size);
    String deactivateUser(UUID userId, User performedBy);


    Page<AuditLogResponse> getAuditLogs(String userId, String action, int page, int size);
}