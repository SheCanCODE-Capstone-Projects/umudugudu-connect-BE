package com.umudugudu.controller;

import com.umudugudu.dto.*;
import com.umudugudu.dto.request.ProfileChangeRequestDto;
import com.umudugudu.dto.request.ReviewChangeRequestDto;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import com.umudugudu.entity.Notification;
import com.umudugudu.service.impl.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PostMapping("/change-request")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ChangeRequestResponse> submitChangeRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileChangeRequestDto dto) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.submitChangeRequest(userId, dto));
    }

    @GetMapping("/change-request/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<ChangeRequestResponse>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.getMyRequests(userId));
    }

    @GetMapping("/change-request/pending")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<List<ChangeRequestResponse>> getPendingRequests() {
        return ResponseEntity.ok(profileService.getPendingRequests());
    }

    @PutMapping("/change-request/{id}/review")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<ChangeRequestResponse> reviewRequest(
            @PathVariable Long id,
            @RequestBody ReviewChangeRequestDto dto) {
        return ResponseEntity.ok(profileService.reviewChangeRequest(id, dto));
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER', 'ISIBO_LEADER', 'CITIZEN')")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.getUnreadNotifications(userId));
    }

    @PatchMapping("/notifications/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markNotificationRead(@PathVariable Long id) {
        profileService.markNotificationRead(id);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(UserDetails userDetails) {
        // If your UserDetails implementation is your User entity:
        if (userDetails instanceof com.umudugudu.entity.User user) {
            return user.getId();
        }
        throw new RuntimeException("Cannot extract user ID from principal");
    }
}
