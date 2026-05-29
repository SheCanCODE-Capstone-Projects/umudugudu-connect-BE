package com.umudugudu.controller;

import com.umudugudu.dto.request.AssignPenaltyRequest;
import com.umudugudu.dto.response.PenaltyResponse;
import com.umudugudu.service.PenaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @PostMapping
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<PenaltyResponse> assign(
            @Valid
            @RequestBody
            AssignPenaltyRequest request
    ) {

        PenaltyResponse response =
                penaltyService.assignPenalty(request);

        return ResponseEntity.status(201)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, String>> myPenalties() {
        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "TODO: return citizen penalties"
                )
        );
    }

    @GetMapping("/village")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<Map<String, String>> villagePenalties() {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "TODO: return all village penalties"
                )
        );
    }

    @GetMapping("/isibo")
    @PreAuthorize("hasAnyRole('ISIBO_LEADER','VILLAGE_LEADER')")
    public ResponseEntity<Map<String, String>> isiboPenalties() {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "TODO: return isibo penalties"
                )
        );
    }

    @PutMapping("/{id}/waive")
    @PreAuthorize("hasRole('VILLAGE_LEADER')")
    public ResponseEntity<Map<String,String>> waive(
            @PathVariable String id,
            @RequestBody Map<String,String> body
    ) {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "TODO: waive penalty " + id
                )
        );
    }
}