package com.umudugudu.controller;

import com.umudugudu.dto.request.BulkAttendanceSyncRequest;
import com.umudugudu.dto.request.MarkAttendanceRequest;
import com.umudugudu.dto.response.AttendanceResponse;
import com.umudugudu.entity.User;
import com.umudugudu.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities/{activityId}/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasRole('ISIBO_LEADER')")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @PathVariable UUID activityId,
            @Valid @RequestBody MarkAttendanceRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                attendanceService.markAttendance(activityId, request, currentUser)
        );
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ISIBO_LEADER')")
    public ResponseEntity<List<AttendanceResponse>> syncOffline(
            @PathVariable UUID activityId,
            @Valid @RequestBody BulkAttendanceSyncRequest request,
            @AuthenticationPrincipal User currentUser) {

        request.setActivityId(activityId);
        return ResponseEntity.ok(
                attendanceService.syncOfflineAttendance(request, currentUser)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @PathVariable UUID activityId) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceForActivity(activityId)
        );
    }

    @GetMapping("/absent")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAbsentMembers(
            @PathVariable UUID activityId) {

        return ResponseEntity.ok(
                attendanceService.getAbsentMembers(activityId)
        );
    }
}