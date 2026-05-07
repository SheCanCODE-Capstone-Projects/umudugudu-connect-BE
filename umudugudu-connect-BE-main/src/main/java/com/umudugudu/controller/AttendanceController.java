package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Attendance marking and retrieval.
 *
 * POST /api/v1/activities/{id}/attendance — bulk submit attendance (ISIBO_LEADER)
 * GET  /api/v1/activities/{id}/attendance — get attendance records (ISIBO_LEADER+)
 *
 * Supports offline sync: client submits buffered records with synced_offline=true.
 * TODO: Inject AttendanceService and implement.
 */
@RestController
@RequestMapping("/api/v1/activities/{activityId}/attendance")
public class AttendanceController {

    @PostMapping
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> submit(@PathVariable String activityId,
                                                       @RequestBody Map<String, Object> body) {
        // TODO: AttendanceService.submitBulk(activityId, records, markedBy)
        return ResponseEntity.status(201).body(Map.of("message", "TODO: save attendance records"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<Map<String, String>> get(@PathVariable String activityId) {
        // TODO: AttendanceService.getByActivity(activityId)
        return ResponseEntity.ok(Map.of("message", "TODO: return attendance for " + activityId));
    }
}
