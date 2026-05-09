package com.umudugudu.controller;

import com.umudugudu.entity.Notification;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.impl.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ProfileService profileService;
    private final UserRepository userRepository; // ← ADDED

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER', 'ISIBO_LEADER', 'CITIZEN')")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.getUnreadNotifications(userId));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        profileService.markNotificationRead(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/announce")
//    @PreAuthorize("hasAnyRole('VILLAGE_LEADER', 'ISIBO_LEADER')")
//    public ResponseEntity<String> announce(@org.springframework.web.bind.annotation.RequestBody
//                                           java.util.Map<String, Object> body) {
//        return ResponseEntity.status(501).body("Announcement feature not yet implemented.");
//    }

    private Long extractUserId(UserDetails userDetails) {
        if (userDetails instanceof com.umudugudu.entity.User user) {
            return user.getId();
        }
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email))
                .getId();
    }
}