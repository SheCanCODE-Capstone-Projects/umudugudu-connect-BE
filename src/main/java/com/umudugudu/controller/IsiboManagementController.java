package com.umudugudu.controller;

import com.umudugudu.dto.request.AssignMembersRequest;
import com.umudugudu.dto.request.CreateIsiboRequest;
import com.umudugudu.dto.response.IsiboResponse;
import com.umudugudu.service.impl.IsiboManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/village-leader/isibos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VILLAGE_LEADER')")
public class IsiboManagementController {

    private final IsiboManagementService service;
    
    @PostMapping
    public ResponseEntity<IsiboResponse> createIsibo(
            @Valid @RequestBody CreateIsiboRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createIsibo(request));
    }
    
    @GetMapping
    public ResponseEntity<List<IsiboResponse>> listIsibos() {
        return ResponseEntity.ok(service.listMyIsibos());
    }
    
    @GetMapping("/{isiboId}")
    public ResponseEntity<IsiboResponse> getIsibo(@PathVariable UUID isiboId) {
        return ResponseEntity.ok(service.getIsibo(isiboId));
    }


    @PostMapping("/{isiboId}/members")
    public ResponseEntity<IsiboResponse> assignMembers(
            @PathVariable UUID isiboId,
            @Valid @RequestBody AssignMembersRequest request) {
        return ResponseEntity.ok(service.assignMembers(isiboId, request));
    }

    @DeleteMapping("/{isiboId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID isiboId,
            @PathVariable UUID memberId) {
        service.removeMember(isiboId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{isiboId}/leader/{userId}")
    public ResponseEntity<IsiboResponse> assignLeader(
            @PathVariable UUID isiboId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(service.assignIsiboLeader(isiboId, userId));
    }

    @GetMapping("/unassigned-members")
    public ResponseEntity<List<IsiboResponse.MemberInfo>> getUnassigned() {
        return ResponseEntity.ok(service.getUnassignedMembers());
    }
    @PutMapping("/citizens/{userId}/promote")
    public ResponseEntity<?> promoteToIsiboLeader(@PathVariable UUID userId) {
        return ResponseEntity.ok(Map.of("message", service.promoteCitizenToIsiboLeader(userId)));
    }
}