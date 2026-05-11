package com.umudugudu.controller;

import com.umudugudu.dto.request.ActivityRequest;
import com.umudugudu.dto.response.ActivityResponse;
import com.umudugudu.security.UserDetailsServiceImpl;
import com.umudugudu.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Activities", description = "Village activity management")
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService        activityService;
    private final UserDetailsServiceImpl userDetailsService;

    @Operation(summary = "Create a new activity (Village Leader only)")
    @PostMapping
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<ActivityResponse> create(
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = userDetailsService.getUserIdByUsername(principal.getUsername());
        ActivityResponse response = activityService.create(request, userId);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "List all activities (paginated)")
    @GetMapping
    public ResponseEntity<Page<ActivityResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(activityService.list(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get a single activity by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getById(id));
    }

    @Operation(summary = "Update an activity (Village Leader only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<ActivityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = userDetailsService.getUserIdByUsername(principal.getUsername());
        return ResponseEntity.ok(activityService.update(id, request, userId));
    }
}
