package com.umudugudu.controller;

import com.umudugudu.dto.request.ChangeRequestReviewRequest;
import com.umudugudu.dto.request.ChangeRequestSubmitRequest;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import com.umudugudu.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // GET /api/v1/profile — all roles
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(profileService.getProfile(principal.getUsername()));
    }

    // POST /api/v1/profile/change-requests — CITIZEN only
    @PostMapping("/change-requests")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ChangeRequestResponse> submitChangeRequest(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangeRequestSubmitRequest request) {
        return ResponseEntity.status(201)
                .body(profileService.submitChangeRequest(principal.getUsername(), request));
    }

    // GET /api/v1/profile/change-requests/my — citizen's own history
    @GetMapping("/change-requests/my")
    public ResponseEntity<List<ChangeRequestResponse>> myChangeRequests(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
                profileService.myChangeRequests(principal.getUsername()));
    }

    // GET /api/v1/profile/change-requests/pending — VILLAGE_LEADER queue
    @GetMapping("/change-requests/pending")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Page<ChangeRequestResponse>> getPendingRequests(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                profileService.getPendingRequests(principal.getUsername(), page, size));
    }

    // PUT /api/v1/profile/change-requests/{id} — VILLAGE_LEADER approve/reject
    @PutMapping("/change-requests/{id}")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<ChangeRequestResponse> reviewChangeRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangeRequestReviewRequest review) {
        return ResponseEntity.ok(
                profileService.reviewChangeRequest(id, principal.getUsername(), review));
    }
}
