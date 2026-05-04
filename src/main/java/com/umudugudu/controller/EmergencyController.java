package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Emergency reporting and broadcasting.
 *
 * POST /api/v1/emergency/report            — citizen reports emergency
 * GET  /api/v1/emergency                   — list emergency reports (VILLAGE_LEADER+)
 * POST /api/v1/emergency/{id}/broadcast    — verify + broadcast to all (VILLAGE_LEADER)
 *
 * TODO: Inject EmergencyService, NotificationService and implement.
 */
@RestController
@RequestMapping("/api/v1/emergency")
public class EmergencyController {

    @PostMapping("/report")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Map<String, String>> report(@RequestBody Map<String, Object> body) {
        // body: { type: FLOOD|HEALTH|FIRE|OTHER, description, location (optional) }
        // TODO: EmergencyService.report(body, reporterId)
        //       — saves report, notifies Village Leader via push + SMS immediately
        return ResponseEntity.status(201).body(Map.of("message", "TODO: save emergency report + alert leader"));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<Map<String, String>> list() {
        return ResponseEntity.ok(Map.of("message", "TODO: return emergency reports for village"));
    }

    @PostMapping("/{id}/broadcast")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> broadcast(@PathVariable String id) {
        // TODO: EmergencyService.broadcast(id) — sends emergency alert to ALL village citizens
        return ResponseEntity.ok(Map.of("message", "TODO: broadcast emergency " + id + " to all citizens"));
    }
}
