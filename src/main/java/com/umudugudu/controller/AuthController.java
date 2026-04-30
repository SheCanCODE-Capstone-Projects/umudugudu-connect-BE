package com.umudugudu.controller;

import com.umudugudu.dto.response.AuthResponse;
import com.umudugudu.service.AuthService;
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

    private AuthService authService;

    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phoneNumber");
        authService.sendOtp(phone);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phoneNumber");
        String code = body.get("code");

        AuthResponse response = authService.verifyOtp(phone, code);

        return ResponseEntity.ok(Map.of(
                "accessToken", response.getAccessToken(),
                "refreshToken", response.getRefreshToken(),
                "user", response.getUser()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", response.getAccessToken()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me() {
        // TODO: return authenticated user from SecurityContext
        return ResponseEntity.ok(Map.of("message", "TODO: return user profile"));
    }
}
