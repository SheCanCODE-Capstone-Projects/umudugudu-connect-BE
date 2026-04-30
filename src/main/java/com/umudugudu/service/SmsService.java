package com.umudugudu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Sends SMS messages via Africa's Talking REST API.
 *
 * Uses a plain HTTP call instead of the SDK so there is no compile-time
 * dependency on the Africa's Talking jar (which may not be available in
 * the local Maven repo during development).
 *
 * In dev mode (no credentials configured) the message is logged to the
 * console so the OTP flow can be tested without a real SMS account.
 */
@Service
@Slf4j
public class SmsService {

    private static final String AT_SMS_URL =
            "https://api.africastalking.com/version1/messaging";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.africas-talking.api-key:}")
    private String apiKey;

    @Value("${app.africas-talking.username:}")
    private String username;

    @Value("${app.africas-talking.sender-id:UmuduguduConnect}")
    private String senderId;

    /**
     * Sends an SMS to the given phone number.
     * Falls back to console logging when credentials are not configured.
     */
    public void send(String phoneNumber, String message) {
        if (apiKey == null || apiKey.isBlank() || username == null || username.isBlank()) {
            log.info("[DEV SMS] To: {} | Message: {}", phoneNumber, message);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("apiKey", apiKey);
            headers.set("Accept", "application/json");

            String body = "username=" + username
                    + "&to=" + phoneNumber
                    + "&message=" + message
                    + "&from=" + senderId;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(AT_SMS_URL, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("SMS sent to {}", phoneNumber);
            } else {
                log.warn("SMS to {} returned status {}", phoneNumber, response.getStatusCode());
            }
        } catch (Exception e) {
            // SMS failure must not block the OTP flow
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
        }
    }
}
