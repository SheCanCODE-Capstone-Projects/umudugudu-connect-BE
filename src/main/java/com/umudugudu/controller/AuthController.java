package com.umudugudu.controller;

import com.umudugudu.dto.request.*;
import com.umudugudu.dto.response.AuthResponse;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/otp/request")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest request) {
        String phone = request.getPhoneNumber();
        authService.sendOtp(phone);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/phone-otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequestPhonenumber request) {
        String phone = request.getPhoneNumber();
        String code = request.getOtp();


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
    public ResponseEntity<?> me() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = authentication.getName();

        var user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();

        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole() != null ? user.getRole().name() : null);
        response.put("message", "User fetched successfully");

        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/email")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/phone")
    public ResponseEntity<AuthResponse> loginWithPhone(
            @RequestBody PhoneLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithPhone(request));
    }

    @PostMapping("/email-otp/verify")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody OtpVerifyRequestEmail request) {

        String email = request.getEmail();
        String code = request.getOtp();

        String message = authService.verifyEmailOtp(email, code);

        return ResponseEntity.ok(Map.of(
                "message", message
        ));
    }
    @PostMapping("/email/resend-otp")
    public ResponseEntity<String> resendEmailOtp(@RequestBody ResendOtpRequest request) {
        authService.resendEmailOtp(request.getEmail());
        return ResponseEntity.ok("New OTP sent to your email");
    }

}
