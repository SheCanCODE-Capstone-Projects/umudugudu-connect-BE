package com.umudugudu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication — OTP-based phone login.
 *
 * POST /api/v1/auth/otp/request  — send OTP via SMS
 * POST /api/v1/auth/otp/verify   — verify OTP, return JWT
 * POST /api/v1/auth/refresh      — exchange refresh token
 * GET  /api/v1/auth/me           — return authenticated user profile
 *
 * TODO: Inject OtpService, JwtUtils, UserService and implement.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestBody Map<String, String> body) {
        // TODO: validate phoneNumber, call OtpService.sendOtp(phoneNumber)
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        // TODO: OtpService.verify(phoneNumber, code) → load user → generate JWT
        return ResponseEntity.ok(Map.of("message", "TODO: return accessToken, refreshToken, user"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        // TODO: validate refreshToken → issue new accessToken
        return ResponseEntity.ok(Map.of("message", "TODO: return new accessToken"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me() {
        // TODO: return authenticated user from SecurityContext
        return ResponseEntity.ok(Map.of("message", "TODO: return user profile"));
    }
}
