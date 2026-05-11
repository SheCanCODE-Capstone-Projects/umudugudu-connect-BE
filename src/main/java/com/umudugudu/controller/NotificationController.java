package com.umudugudu.controller;

import com.umudugudu.dto.response.NotificationResponse;
import com.umudugudu.entity.User;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.security.UserDetailsServiceImpl;
import com.umudugudu.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Notifications", description = "In-app notifications")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService    notificationService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository         userRepository;

    @Operation(summary = "Get my notifications (paginated)")
    @GetMapping("/my")
    public ResponseEntity<Page<NotificationResponse>> myNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = userDetailsService.getUserIdByUsername(principal.getUsername());
        return ResponseEntity.ok(
                notificationService.getMyNotifications(userId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Mark a notification as read")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = userDetailsService.getUserIdByUsername(principal.getUsername());
        notificationService.markRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Register or update FCM device token for push notifications")
    @PutMapping("/fcm-token")
    public ResponseEntity<Map<String, String>> registerFcmToken(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal) {

        String token = body.get("fcmToken");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "fcmToken is required"));
        }

        Long userId = userDetailsService.getUserIdByUsername(principal.getUsername());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFcmToken(token);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "FCM token registered"));
    }
}
