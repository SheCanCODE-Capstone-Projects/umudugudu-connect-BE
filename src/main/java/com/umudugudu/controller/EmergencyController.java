package com.umudugudu.controller;

import com.umudugudu.dto.request.CreateEmergencyRequest;
import com.umudugudu.entity.EmergencyReport;
import com.umudugudu.repository.EmergencyRepository;
import com.umudugudu.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;


    @PostMapping("/report")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Map<String, String>> reportEmergency(
            @RequestBody CreateEmergencyRequest request
    ) {
        emergencyService.reportEmergency(request);

        return ResponseEntity.status(201).body(
                Map.of("message", "Emergency reported successfully")
        );
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<?> listEmergencies() {
        return ResponseEntity.ok(
                emergencyService.getAllEmergencies()
        );
    }


    @PostMapping("/{id}/broadcast")
    public ResponseEntity<?> broadcast(@PathVariable UUID id) {

        EmergencyReport report = emergencyService.getEmergencyById(id);

        emergencyService.broadcastEmergency(
                report.getDescription(),
                report.getVillageId()
        );

        return ResponseEntity.ok("Broadcast sent");
    }
}
