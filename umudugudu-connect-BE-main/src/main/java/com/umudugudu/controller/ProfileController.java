package com.umudugudu.controller;

import com.umudugudu.dto.request.ProfileChangeRequestDto;
import com.umudugudu.dto.request.ReviewChangeRequestDto;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PostMapping("/change-request")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ChangeRequestResponse> submitChangeRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileChangeRequestDto dto) {
        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(profileService.submitChangeRequest(userId, dto));
    }

    @GetMapping("/change-request/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<ChangeRequestResponse>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
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
            @PathVariable UUID id,
            @RequestBody ReviewChangeRequestDto dto) {
        return ResponseEntity.ok(profileService.reviewChangeRequest(id, dto));
    }

    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof com.umudugudu.entity.User user) {
            return user.getId();
        }
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email))
                .getId();
    }
}