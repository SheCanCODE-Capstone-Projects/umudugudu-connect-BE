package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Citizen service requests (Ubudehe, assistance, documents).
 *
 * POST /api/v1/service-requests            — submit request (CITIZEN)
 * GET  /api/v1/service-requests/my         — citizen's own requests
 * GET  /api/v1/service-requests/queue      — leader review queue
 * PUT  /api/v1/service-requests/{id}/review — approve/reject/info-required
 *
 * TODO: Inject ServiceRequestService and implement.
 */
@RestController
@RequestMapping("/api/v1/service-requests")
public class ServiceRequestController {

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Map<String, String>> submit(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(201).body(Map.of("message", "TODO: create service request"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Map<String, String>> myRequests() {
        return ResponseEntity.ok(Map.of("message", "TODO: return citizen's service requests"));
    }

    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<Map<String, String>> queue() {
        return ResponseEntity.ok(Map.of("message", "TODO: return pending requests for this leader"));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> review(@PathVariable String id,
                                                       @RequestBody Map<String, String> body) {
        // body: { decision: APPROVED|REJECTED|INFO_REQUIRED, response: "..." }
        return ResponseEntity.ok(Map.of("message", "TODO: update request " + id + " status"));
    }
}
