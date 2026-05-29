package com.umudugudu.controller;


import com.umudugudu.dto.request.SmsCommandRequest;
import com.umudugudu.dto.response.SmsResponse;
import com.umudugudu.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/inbound")
    public ResponseEntity<Map<String, String>> handleIncomingSms(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        try {
            log.info("Received SMS from {}: {}", phoneNumber, message);

            SmsCommandRequest request = new SmsCommandRequest();
            request.setPhoneNumber(phoneNumber);
            request.setCommand(message);

            SmsResponse response = smsService.processSmsCommand(request);

            if (response.isSuccess()) {

                smsService.sendSms(phoneNumber, response.getFullResponse());
                log.info("SMS response sent to {}", phoneNumber);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "received",
                    "message", "SMS command processed"
            ));
        } catch (Exception e) {
            log.error("Error processing incoming SMS: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> sendSms(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        try {
            boolean success = smsService.sendSms(phoneNumber, message);

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "SMS sent to " + phoneNumber
                ));
            } else {
                return ResponseEntity.status(400).body(Map.of(
                        "status", "failed",
                        "message", "Failed to send SMS"
                ));
            }
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/command")
    public ResponseEntity<SmsResponse> processSmsCommand(
            @RequestBody SmsCommandRequest request) {
        try {
            SmsResponse response = smsService.processSmsCommand(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing SMS command: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}

