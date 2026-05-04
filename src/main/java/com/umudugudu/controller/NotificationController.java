package com.umudugudu.controller;

import com.umudugudu.dto.request.NotificationRequest;
import com.umudugudu.dto.response.NotificationResponse;
import com.umudugudu.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Notifications and village announcements.
 *
 * POST /api/v1/notifications/announce   — broadcast announcement (VILLAGE_LEADER)
 * GET  /api/v1/notifications/my         — my notifications (all roles)
 * PUT  /api/v1/notifications/{id}/read  — mark as read
 * GET  /api/v1/notifications/unread-count — get unread count
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Broadcast announcement with SMS fallback
     */
    @PostMapping("/announce")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ISIBO_LEADER')")
    public ResponseEntity<Map<String, String>> announce(
            @RequestBody NotificationRequest body,
            Authentication authentication) {
        try {
            log.info("Broadcasting announcement from {}", authentication.getName());
            notificationService.broadcastWithFallback(body);

            return ResponseEntity.status(201).body(Map.of(
                    "status", "success",
                    "message", "Announcement broadcast (FCM + SMS fallback)"
            ));
        } catch (Exception e) {
            log.error("Error broadcasting announcement: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get my notifications (paginated)
     */
    @GetMapping("/my")
    public ResponseEntity<Page<NotificationResponse>> myNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            // TODO: Extract actual user ID from JWT token
            UUID userId = UUID.fromString("dummy-id"); // Replace with actual user ID

            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationResponse> notifications = notificationService
                    .getMyNotifications(userId, pageable);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching notifications: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(
            @PathVariable UUID id,
            Authentication authentication) {
        try {
            // TODO: Extract actual user ID from JWT token
            UUID userId = UUID.fromString("dummy-id"); // Replace with actual user ID

            notificationService.markAsRead(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Notification marked as read"
            ));
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Get unread notification count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            Authentication authentication) {
        try {
            // TODO: Extract actual user ID from JWT token
            UUID userId = UUID.fromString("dummy-id"); // Replace with actual user ID

            long count = notificationService.getUnreadCount(userId);

            return ResponseEntity.ok(Map.of(
                    "unreadCount", count
            ));
        } catch (Exception e) {
            log.error("Error getting unread count: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}

