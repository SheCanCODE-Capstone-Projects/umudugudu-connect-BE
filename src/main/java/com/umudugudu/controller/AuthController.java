package com.umudugudu.controller;

import com.umudugudu.dto.*;
import com.umudugudu.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * E1 — Authentication & User Management
 *
 * POST /api/v1/auth/register    — register with email + password
 * POST /api/v1/auth/login       — login with email + password → JWT
 * POST /api/v1/auth/otp/request — send 6-digit OTP via SMS
 * POST /api/v1/auth/otp/verify  — verify OTP → return JWT
 * POST /api/v1/auth/refresh     — exchange refresh token
 * GET  /api/v1/auth/me          — return authenticated user profile
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── POST /register ────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequestDto request) {

        AuthResponseDto response = authService.register(
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                request.password());

        return ResponseEntity.status(201).body(Map.of(
                "success",   true,
                "data",      response,
                "message",   "Registration successful",
                "timestamp", Instant.now().toString()
        ));
    }

    // ── POST /login ───────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequestDto request) {

        AuthResponseDto response = authService.login(
                request.email(), request.password());

        return ResponseEntity.ok(Map.of(
                "success",   true,
                "data",      response,
                "message",   "Login successful",
                "timestamp", Instant.now().toString()
        ));
    }

    // ── POST /otp/request ─────────────────────────────────────────────────────

    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, Object>> requestOtp(
            @Valid @RequestBody OtpRequestDto request) {

        authService.requestOtp(request.phoneNumber());

        return ResponseEntity.ok(Map.of(
                "success",   true,
                "message",   "OTP sent successfully",
                "timestamp", Instant.now().toString()
        ));
    }

    // ── POST /otp/verify ──────────────────────────────────────────────────────

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @Valid @RequestBody OtpVerifyDto request) {

        AuthResponseDto response = authService.verifyOtp(
                request.phoneNumber(), request.code());

        return ResponseEntity.ok(Map.of(
                "success",   true,
                "data",      response,
                "message",   "Authentication successful",
                "timestamp", Instant.now().toString()
        ));
    }

    // ── POST /refresh ─────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @Valid @RequestBody RefreshTokenDto request) {

        AuthResponseDto response = authService.refresh(request.refreshToken());

        return ResponseEntity.ok(Map.of(
                "success",   true,
                "data",      response,
                "message",   "Token refreshed",
                "timestamp", Instant.now().toString()
        ));
    }

    // ── GET /me ───────────────────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @AuthenticationPrincipal UserDetails principal) {

        UserProfileDto profile = authService.getProfile(principal.getUsername());

        return ResponseEntity.ok(Map.of(
                "success",   true,
                "data",      profile,
                "message",   "OK",
                "timestamp", Instant.now().toString()
        ));
    }
}
