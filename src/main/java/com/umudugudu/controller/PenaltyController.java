package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Penalty assignment and retrieval.
 *
 * POST /api/v1/penalties             — assign penalty (VILLAGE_LEADER)
 * GET  /api/v1/penalties/my          — citizen's own penalties
 * GET  /api/v1/penalties/village     — all village penalties (VILLAGE_LEADER)
 * GET  /api/v1/penalties/isibo       — isibo penalties (ISIBO_LEADER)
 * PUT  /api/v1/penalties/{id}/waive  — waive penalty (VILLAGE_LEADER)
 *
 * TODO: Inject PenaltyService and implement.
 */
@RestController
@RequestMapping("/api/v1/penalties")
public class PenaltyController {

    @PostMapping
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> assign(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(201).body(Map.of("message", "TODO: assign penalty"));
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, String>> myPenalties() {
        return ResponseEntity.ok(Map.of("message", "TODO: return citizen penalties"));
    }

    @GetMapping("/village")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<Map<String, String>> villagePenalties() {
        return ResponseEntity.ok(Map.of("message", "TODO: return all village penalties"));
    }

    @GetMapping("/isibo")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> isiboPenalties() {
        return ResponseEntity.ok(Map.of("message", "TODO: return isibo penalties"));
    }

    @PutMapping("/{id}/waive")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> waive(@PathVariable String id,
                                                      @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("message", "TODO: waive penalty " + id));
    }
}
