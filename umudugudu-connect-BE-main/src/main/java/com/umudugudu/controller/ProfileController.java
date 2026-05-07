package com.umudugudu.controller;

import com.umudugudu.dto.request.ProfileChangeRequestDTO;
import com.umudugudu.dto.request.ReviewChangeRequestDTO;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import com.umudugudu.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(profileService.getMyProfile(auth.getName()));
    }

    @PostMapping("/change-requests")
    public ResponseEntity<ChangeRequestResponse> submitChangeRequest(
            Authentication auth, @RequestBody ProfileChangeRequestDTO dto) {
        return ResponseEntity.ok(profileService.submitChangeRequest(auth.getName(), dto));
    }

    @GetMapping("/change-requests")
    public ResponseEntity<List<ChangeRequestResponse>> getMyChangeRequests(Authentication auth) {
        return ResponseEntity.ok(profileService.getMyChangeRequests(auth.getName()));
    }

    @GetMapping("/change-requests/pending")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER', 'ADMIN')")
    public ResponseEntity<List<ChangeRequestResponse>> getPendingRequests(Authentication auth) {
        return ResponseEntity.ok(profileService.getPendingChangeRequests(auth.getName()));
    }

    @PutMapping("/change-requests/{id}/review")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER', 'ADMIN')")
    public ResponseEntity<ChangeRequestResponse> reviewChangeRequest(
            Authentication auth, @PathVariable Long id, @RequestBody ReviewChangeRequestDTO dto) {
        return ResponseEntity.ok(profileService.reviewChangeRequest(auth.getName(), id, dto));
    }
}