package com.umudugudu.controller;

import com.umudugudu.dto.request.AssignVillageLeaderRequest;
import com.umudugudu.dto.request.CreateVillageRequest;
import com.umudugudu.dto.request.UpdateRoleRequest;
import com.umudugudu.dto.response.AuditLogResponse;
import com.umudugudu.dto.response.DashboardResponse;
import com.umudugudu.dto.response.UserResponseDTO;
import com.umudugudu.entity.User;
import com.umudugudu.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import com.umudugudu.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    //Aggregated Dashboard

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // User Management

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> users(
            @RequestParam(required = false) String village,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAllUsers(village, role, page, size));
    }

    @GetMapping("/users/search")
    public ResponseEntity<UserResponseDTO> searchUserByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(adminService.findUserByEmail(email));
    }

    @PutMapping("/users/role")
    public ResponseEntity<?> updateRole(@RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(Map.of("message",
                adminService.updateRoleByEmail(request.getEmail(), request.getRole())));
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        // handle both email and phone number login
        User currentUser = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByPhoneNumber(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));

        return ResponseEntity.ok(Map.of("message",
                adminService.deactivateUser(id, currentUser)));
    }

    @PutMapping("/users/assign-village-leader")
    public ResponseEntity<?> assignVillageLeader(
            @RequestBody AssignVillageLeaderRequest request) {
        return ResponseEntity.ok(Map.of("message",
                adminService.assignVillageLeader(request.getEmail(), request.getVillageId())));
    }

    @PostMapping("/villages")
    public ResponseEntity<?> createVillage(@RequestBody CreateVillageRequest request) {
        return ResponseEntity.ok(Map.of("message",
                adminService.createVillage(request.getName())));
    }

    // Audit Logs

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> auditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(
                adminService.getAuditLogs(userId, action, page, size));
    }
}