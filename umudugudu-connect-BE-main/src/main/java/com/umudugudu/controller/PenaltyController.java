package com.umudugudu.controller;

import com.umudugudu.dto.request.AssignPenaltyRequest;
import com.umudugudu.dto.request.ExemptPenaltyRequest;
import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.IsiboPenaltySummaryResponse;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.dto.response.PenaltyResponse;
import com.umudugudu.entity.User;
import com.umudugudu.service.PenaltyFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/penalties")
@RequiredArgsConstructor
@Tag(name = "Penalties", description = "E2.3 penalty flags + E3 penalty assignment, citizen view, isibo summary")
public class PenaltyController {

    private final PenaltyFlagService penaltyFlagService;


    @GetMapping("/activity/{activityId}")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    @Operation(summary = "Get all flags for an activity")
    public ResponseEntity<List<PenaltyFlagResponse>> getFlagsForActivity(
            @PathVariable UUID activityId) {
        return ResponseEntity.ok(penaltyFlagService.getFlagsForActivity(activityId));
    }

    @GetMapping("/activity/{activityId}/pending")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    @Operation(summary = "Get pending (FLAGGED) penalties for an activity")
    public ResponseEntity<List<PenaltyFlagResponse>> getPendingFlags(
            @PathVariable UUID activityId) {
        return ResponseEntity.ok(penaltyFlagService.getPendingFlagsForActivity(activityId));
    }

    @GetMapping("/citizen/{citizenId}")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    @Operation(summary = "Get all flags for a citizen (admin/leader view)")
    public ResponseEntity<List<PenaltyFlagResponse>> getFlagsForCitizen(
            @PathVariable UUID citizenId) {
        return ResponseEntity.ok(penaltyFlagService.getFlagsForCitizen(citizenId));
    }

    @PutMapping("/{flagId}/review")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    @Operation(summary = "Review a penalty flag (CONFIRM or WAIVE)")
    public ResponseEntity<PenaltyFlagResponse> reviewPenalty(
            @PathVariable UUID flagId,
            @Valid @RequestBody ReviewPenaltyRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(penaltyFlagService.reviewPenalty(flagId, request, currentUser));
    }


    @PostMapping("/{flagId}/assign")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    @Operation(summary = "US-3.1 — Assign penalty with amount and reason to an absent citizen")
    public ResponseEntity<PenaltyResponse> assignPenalty(
            @PathVariable UUID flagId,
            @Valid @RequestBody AssignPenaltyRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(penaltyFlagService.assignPenalty(flagId, request, currentUser));
    }

    @PostMapping("/{flagId}/exempt")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    @Operation(summary = "US-3.1 — Mark citizen as EXCUSED before penalty is assigned")
    public ResponseEntity<PenaltyResponse> exemptCitizen(
            @PathVariable UUID flagId,
            @Valid @RequestBody ExemptPenaltyRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(penaltyFlagService.exemptCitizen(flagId, request, currentUser));
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    @Operation(summary = "US-3.2 — Citizen views all their own penalties (UNPAID / PAID / EXCUSED)")
    public ResponseEntity<List<PenaltyResponse>> getMyPenalties(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(penaltyFlagService.getMyPenalties(currentUser));
    }


    @GetMapping("/isibo/summary")
    @PreAuthorize("hasRole('ISIBO_LEADER')")
    @Operation(summary = "US-3.3 — Isibo leader views outstanding penalties across all households")
    public ResponseEntity<IsiboPenaltySummaryResponse> getIsiboPenaltySummary(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(penaltyFlagService.getIsiboPenaltySummary(currentUser));
    }

    @GetMapping("/isibo/summary/household/{citizenId}")
    @PreAuthorize("hasRole('ISIBO_LEADER')")
    @Operation(summary = "US-3.3 — Drill down into one household's individual penalty records")
    public ResponseEntity<List<PenaltyResponse>> getHouseholdPenalties(
            @PathVariable UUID citizenId,
            @AuthenticationPrincipal User currentUser) {

        List<PenaltyResponse> penalties = penaltyFlagService.getMyPenalties(
                // build a lightweight proxy user with just the id set
                buildProxyUser(citizenId));
        return ResponseEntity.ok(penalties);
    }

    private User buildProxyUser(UUID citizenId) {
        User proxy = new User();
        proxy.setId(citizenId);
        return proxy;
    }
}