package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Notifications and village announcements.
 *
 * POST /api/v1/notifications/announce   — broadcast announcement (VILLAGE_LEADER)
 * GET  /api/v1/notifications/my         — my notifications (all roles)
 * PUT  /api/v1/notifications/{id}/read  — mark as read
 *
 * TODO: Inject NotificationService, PushNotifService and implement.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @PostMapping("/announce")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ISIBO_LEADER')")
    public ResponseEntity<Map<String, String>> announce(@RequestBody Map<String, Object> body) {
        // body: { title, message, targetIsibo (optional) }
        // TODO: NotificationService.broadcast(body, senderId) — push + SMS fallback
        return ResponseEntity.status(201).body(Map.of("message", "TODO: broadcast announcement"));
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, String>> myNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(Map.of("message", "TODO: return paginated notifications"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable String id) {
        // TODO: NotificationService.markRead(id, currentUserId)
        return ResponseEntity.ok().build();
    }
}
