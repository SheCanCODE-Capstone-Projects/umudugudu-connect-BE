package com.umudugudu.controller;

import com.umudugudu.dto.request.ReviewServiceRequestDto;
import com.umudugudu.dto.request.SubmitServiceRequestDto;
import com.umudugudu.dto.response.ServiceRequestResponse;
import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/service-requests")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final UserRepository        userRepository;

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ServiceRequestResponse> submit(
            @Valid @RequestBody SubmitServiceRequestDto dto,
            Authentication authentication) {

        User citizen = resolveUser(authentication);
        ServiceRequestResponse response = serviceRequestService.submit(dto, citizen);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<ServiceRequestResponse>> getMyRequests(
            Authentication authentication) {

        User citizen = resolveUser(authentication);
        return ResponseEntity.ok(serviceRequestService.getMyRequests(citizen));
    }

    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER', 'VILLAGE_LEADER', 'ADMIN')")
    public ResponseEntity<List<ServiceRequestResponse>> getPendingQueue(
            Authentication authentication) {

        User leader = resolveUser(authentication);
        return ResponseEntity.ok(serviceRequestService.getPendingQueue(leader));
    }


    @GetMapping("/queue/all")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER', 'VILLAGE_LEADER', 'ADMIN')")
    public ResponseEntity<List<ServiceRequestResponse>> getFullQueue(
            Authentication authentication) {

        User leader = resolveUser(authentication);
        return ResponseEntity.ok(serviceRequestService.getFullQueue(leader));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER', 'VILLAGE_LEADER', 'ADMIN')")
    public ResponseEntity<ServiceRequestResponse> review(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewServiceRequestDto dto,
            Authentication authentication) {

        User leader = resolveUser(authentication);
        return ResponseEntity.ok(serviceRequestService.review(id, dto, leader));
    }


    private User resolveUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + username));
    }
}
