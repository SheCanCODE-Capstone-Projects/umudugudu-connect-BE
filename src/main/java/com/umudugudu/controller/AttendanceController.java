package com.umudugudu.controller;

import com.umudugudu.dto.request.BulkAttendanceSyncRequest;
import com.umudugudu.dto.request.MarkAttendanceRequest;
import com.umudugudu.dto.response.AttendanceResponse;
import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    @PostMapping("/api/activities/{activityId}/attendance")
    @PreAuthorize("hasRole('ISIBO_LEADER')")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @PathVariable UUID activityId,
            @Valid @RequestBody MarkAttendanceRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        return ResponseEntity.ok(
                attendanceService.markAttendance(activityId, request, currentUser)
        );
    }

    @PostMapping("/api/activities/{activityId}/attendance/sync")
    @PreAuthorize("hasRole('ISIBO_LEADER')")
    public ResponseEntity<List<AttendanceResponse>> syncOffline(
            @PathVariable UUID activityId,
            @Valid @RequestBody BulkAttendanceSyncRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        request.setActivityId(activityId);

        return ResponseEntity.ok(
                attendanceService.syncOfflineAttendance(request, currentUser)
        );
    }

    @GetMapping("/api/activities/{activityId}/attendance")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @PathVariable UUID activityId) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceForActivity(activityId)
        );
    }

    @GetMapping("/api/activities/{activityId}/attendance/absent")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAbsentMembers(
            @PathVariable UUID activityId) {

        return ResponseEntity.ok(
                attendanceService.getAbsentMembers(activityId)
        );
    }

    @GetMapping("/api/isibos/{isiboId}/attendance")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceForIsibo(
            @PathVariable UUID isiboId) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceForIsibo(isiboId)
        );
    }

    @GetMapping("/api/citizens/{citizenId}/attendance")
    @PreAuthorize("hasAnyRole('CITIZEN','ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceForCitizen(
            @PathVariable UUID citizenId) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceForCitizen(citizenId)
        );
    }
}
