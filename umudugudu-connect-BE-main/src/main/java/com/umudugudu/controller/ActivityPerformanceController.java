
package com.umudugudu.controller;

import com.umudugudu.dto.response.ActivityPerformanceResponse;
import com.umudugudu.entity.User;
import com.umudugudu.service.ActivityPerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities/performance")
@RequiredArgsConstructor
@Tag(name = "Activity Performance", description = "US-2.4 — Village leader attendance dashboard")
public class ActivityPerformanceController {

    private final ActivityPerformanceService performanceService;

    @GetMapping
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    @Operation(summary = "Get performance for all village activities",
            description = "Returns total invited, present, absent, and participation % per activity with isibo drill-down.")
    public ResponseEntity<List<ActivityPerformanceResponse>> getAllPerformance(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(performanceService.getAllActivityPerformance(currentUser));
    }

    @GetMapping("/{activityId}")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    @Operation(summary = "Get performance for a single activity",
            description = "Returns totals and per-isibo household attendance for the given activity.")
    public ResponseEntity<ActivityPerformanceResponse> getPerformance(
            @PathVariable UUID activityId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(performanceService.getActivityPerformance(activityId, currentUser));
    }
}