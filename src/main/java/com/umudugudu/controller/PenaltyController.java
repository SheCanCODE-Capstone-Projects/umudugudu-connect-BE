package com.umudugudu.controller;

import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.entity.User;
import com.umudugudu.service.PenaltyFlagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyFlagService penaltyFlagService;


    @GetMapping("/activities/{activityId}/penalties")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<PenaltyFlagResponse>> getFlagsForActivity(
            @PathVariable UUID activityId) {

        return ResponseEntity.ok(
                penaltyFlagService.getFlagsForActivity(activityId)
        );
    }

    @GetMapping("/activities/{activityId}/penalties/pending")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<PenaltyFlagResponse>> getPendingFlags(
            @PathVariable UUID activityId) {

        return ResponseEntity.ok(
                penaltyFlagService.getPendingFlagsForActivity(activityId)
        );
    }

    @GetMapping("/citizens/{citizenId}/penalties")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<PenaltyFlagResponse>> getFlagsForCitizen(
            @PathVariable UUID citizenId) {

        return ResponseEntity.ok(
                penaltyFlagService.getFlagsForCitizen(citizenId)
        );
    }

    @PatchMapping("/penalties/{flagId}/review")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<PenaltyFlagResponse> reviewPenalty(
            @PathVariable UUID flagId,
            @Valid @RequestBody ReviewPenaltyRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                penaltyFlagService.reviewPenalty(flagId, request, currentUser)
        );
    }
}