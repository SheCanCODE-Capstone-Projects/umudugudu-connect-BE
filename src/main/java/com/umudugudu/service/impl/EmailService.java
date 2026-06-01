package com.umudugudu.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.brevo.api-key}")
    private String brevoApiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtpEmail(String to, String otp) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = Map.of(
                    "sender", Map.of(
                            "name", "Umudugudu Connect",
                            "email", fromEmail
                    ),
                    "to", List.of(Map.of("email", to)),
                    "subject", "Umudugudu Connect – Your Verification Code",
                    "textContent",
                    "Dear User,\n\n" +
                            "Thank you for registering with Umudugudu Connect.\n\n" +
                            "Your One-Time Password (OTP) is:\n\n" +
                            "        " + otp + "\n\n" +
                            "This code is valid for 5 minutes. Do not share it with anyone.\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "The Umudugudu Connect Team"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("OTP email sent successfully to {}", to);
            } else {
                log.error("Brevo API returned: {}", response.getStatusCode());
                throw new RuntimeException("Could not send OTP email. Please try again.");
            }

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Could not send OTP email. Please try again.");
        }
    }
}