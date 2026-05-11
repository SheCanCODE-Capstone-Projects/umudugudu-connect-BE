package com.umudugudu.service.impl;

import com.umudugudu.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Sends push notifications via the FCM HTTP v1 API (legacy server-key approach).
 *
 * If FCM_SERVER_KEY is not configured the send is skipped and a warning is logged,
 * so the application still starts cleanly in dev without credentials.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    @Value("${app.fcm.server-key:}")
    private String serverKey;

    private final RestTemplate restTemplate;

    @Override
    public void sendPush(String fcmToken, String title, String body, String deepLink) {
        if (serverKey == null || serverKey.isBlank()) {
            log.warn("FCM server key not configured — skipping push to token {}", fcmToken);
            return;
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            log.debug("Recipient has no FCM token — skipping push notification");
            return;
        }

        Map<String, Object> payload = Map.of(
                "to", fcmToken,
                "notification", Map.of(
                        "title", title,
                        "body", body
                ),
                "data", Map.of(
                        "deepLink", deepLink != null ? deepLink : ""
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "key=" + serverKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(FCM_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("FCM push sent to token {}", fcmToken);
            } else {
                log.warn("FCM push failed for token {}: {}", fcmToken, response.getBody());
            }
        } catch (Exception e) {
            log.error("FCM push error for token {}: {}", fcmToken, e.getMessage());
        }
    }
}
