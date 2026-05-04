package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Activity management (Umuganda, Imihigo).
 *
 * POST   /api/v1/activities              — create activity (VILLAGE_LEADER)
 * GET    /api/v1/activities              — list village activities (all roles)
 * GET    /api/v1/activities/{id}         — single activity with attendance summary
 * PUT    /api/v1/activities/{id}         — update activity (VILLAGE_LEADER)
 *
 * TODO: Inject ActivityService and implement.
 */
@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    @PostMapping
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, Object> body) {
        // TODO: ActivityService.create(request, currentUserId)
        return ResponseEntity.status(201).body(Map.of("message", "TODO: create activity"));
    }
    @GetMapping
    public ResponseEntity<Map<String, String>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // TODO: ActivityService.listForVillage(currentUserId, pageable)
        return ResponseEntity.ok(Map.of("message", "TODO: return paginated activities"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> get(@PathVariable String id) {
        // TODO: ActivityService.getById(id)
        return ResponseEntity.ok(Map.of("message", "TODO: return activity " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> update(@PathVariable String id,
                                                       @RequestBody Map<String, Object> body) {
        // TODO: ActivityService.update(id, request)
        return ResponseEntity.ok(Map.of("message", "TODO: update activity " + id));
    }
}
