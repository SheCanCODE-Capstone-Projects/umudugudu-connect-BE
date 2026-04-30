package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin (District / MINALOC) endpoints.
 *
 * GET /api/v1/admin/dashboard          — aggregated KPIs across villages
 * GET /api/v1/admin/users              — list all users (filterable)
 * PUT /api/v1/admin/users/{id}/role    — update user role
 * PUT /api/v1/admin/users/{id}/deactivate — deactivate account
 * GET /api/v1/admin/audit-logs         — paginated audit log
 *
 * All endpoints require ROLE_ADMIN (enforced in SecurityConfig + @PreAuthorize).
 * TODO: Inject AdminService, UserService, AuditLogService and implement.
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        // TODO: AdminService.getDashboardStats() — attendance %, payments, open requests by village
        return ResponseEntity.ok(Map.of("message", "TODO: return aggregated KPIs"));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, String>> users(
            @RequestParam(required = false) String village,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(Map.of("message", "TODO: return paginated users list"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, String>> updateRole(@PathVariable String id,
                                                           @RequestBody Map<String, String> body) {
        // body: { role: CITIZEN|ISIBO_LEADER|VILLAGE_LEADER|ADMIN }
        // TODO: UserService.updateRole(id, newRole) + AuditLogService.log(...)
        return ResponseEntity.ok(Map.of("message", "TODO: update role for user " + id));
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivate(@PathVariable String id) {
        // TODO: UserService.deactivate(id) + AuditLogService.log(...)
        return ResponseEntity.ok(Map.of("message", "TODO: deactivate user " + id));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Map<String, String>> auditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(Map.of("message", "TODO: return paginated audit logs"));
    }
}
