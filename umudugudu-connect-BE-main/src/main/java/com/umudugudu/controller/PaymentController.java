package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mobile Money payment flows.
 *
 * POST /api/v1/payments/initiate          — initiate MoMo payment (CITIZEN)
 * POST /api/v1/payments/callback/mtn      — MTN callback webhook (public, HMAC-signed)
 * POST /api/v1/payments/callback/airtel   — Airtel callback webhook (public, HMAC-signed)
 * GET  /api/v1/payments/village           — village payment records (VILLAGE_LEADER)
 * GET  /api/v1/payments/export            — CSV export (ADMIN)
 *
 * IMPORTANT: Verify HMAC-SHA256 signature on all callbacks before processing.
 * TODO: Inject PaymentService, MtnMomoService, AirtelMoneyService and implement.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Map<String, Object>> initiate(@RequestBody Map<String, Object> body) {
        // TODO: PaymentService.initiate(penaltyId, method, phoneNumber)
        // Returns: { paymentId, status: "PENDING", message: "Confirm on your phone" }
        return ResponseEntity.status(201).body(Map.of("message", "TODO: initiate payment + return paymentId"));
    }

    @PostMapping("/callback/mtn")
    public ResponseEntity<Void> mtnCallback(
            @RequestHeader(value = "X-Callback-Signature", required = false) String signature,
            @RequestBody Map<String, Object> payload) {
        // TODO: verify signature, PaymentService.processMtnCallback(payload)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback/airtel")
    public ResponseEntity<Void> airtelCallback(
            @RequestHeader(value = "X-Airtel-Signature", required = false) String signature,
            @RequestBody Map<String, Object> payload) {
        // TODO: verify signature, PaymentService.processAirtelCallback(payload)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/village")
    @PreAuthorize("hasAnyRole('VILLAGE_LEADER','ADMIN')")
    public ResponseEntity<Map<String, String>> villagePayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(Map.of("message", "TODO: return paginated village payments"));
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam String from,
            @RequestParam String to) {
        // TODO: PaymentService.exportCsv(from, to) → return CSV bytes
        // Set headers: Content-Type: text/csv, Content-Disposition: attachment; filename=payments.csv
        return ResponseEntity.ok().build();
    }
}
