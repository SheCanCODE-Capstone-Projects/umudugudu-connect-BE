package com.umudugudu.service;

import com.umudugudu.exception.MaxAttemptsExceededException;
import com.umudugudu.exception.OtpExpiredException;
import com.umudugudu.exception.OtpInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OTP lifecycle manager.
 *
 * Uses an in-memory store (ConcurrentHashMap) so the app works without Redis
 * in dev mode. In production, swap this for a Redis-backed implementation.
 *
 * In-memory layout per phone number:
 *   code       — the 6-digit OTP
 *   expiresAt  — Instant when the OTP expires
 *   attempts   — how many wrong attempts have been made
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    // phone → OTP entry
    private final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();

    private final SmsService smsService;

    @Value("${app.otp.ttl-seconds:300}")
    private long ttlSeconds;

    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;

    // ── Public API ────────────────────────────────────────────────────────────

    public void sendOtp(String phoneNumber) {
        String code = generateCode();
        store.put(phoneNumber, new OtpEntry(code, Instant.now().plusSeconds(ttlSeconds), 0));
        smsService.send(phoneNumber,
                "Your Umudugudu Connect verification code is: " + code +
                ". Valid for 5 minutes. Do not share this code.");
        log.debug("OTP generated for {}", phoneNumber);
    }

    public void verifyOtp(String phoneNumber, String submittedCode) {
        OtpEntry entry = store.get(phoneNumber);

        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            store.remove(phoneNumber);
            throw new OtpExpiredException();
        }

        int attempts = entry.attempts() + 1;

        if (attempts > maxAttempts) {
            store.remove(phoneNumber);
            throw new MaxAttemptsExceededException();
        }

        if (!entry.code().equals(submittedCode)) {
            // update attempt count
            store.put(phoneNumber, new OtpEntry(entry.code(), entry.expiresAt(), attempts));
            int remaining = maxAttempts - attempts;
            if (remaining <= 0) {
                store.remove(phoneNumber);
                throw new MaxAttemptsExceededException();
            }
            throw new OtpInvalidException(remaining);
        }

        // ✅ correct — clean up
        store.remove(phoneNumber);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String generateCode() {
        return String.valueOf(100_000 + RANDOM.nextInt(900_000));
    }

    private record OtpEntry(String code, Instant expiresAt, int attempts) {}
}
