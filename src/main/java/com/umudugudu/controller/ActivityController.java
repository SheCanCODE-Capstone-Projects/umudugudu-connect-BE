package com.umudugudu.controller;

import com.umudugudu.dto.request.CreateActivityRequest;
import com.umudugudu.entity.Activity;
import com.umudugudu.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    @PostMapping
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Activity> createActivity(
            @Valid @RequestBody CreateActivityRequest request
    ) {

        // TODO: later extract from authenticated user
        UUID leaderId = UUID.randomUUID();

        Activity activity = activityService.createActivity(request, leaderId);

        return new ResponseEntity<>(activity, HttpStatus.CREATED);
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
