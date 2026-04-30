//package com.umudugudu.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
///**
// * Authentication — OTP-based phone login.
// *
// * POST /api/v1/auth/otp/request  — send OTP via SMS
// * POST /api/v1/auth/otp/verify   — verify OTP, return JWT
// * POST /api/v1/auth/refresh      — exchange refresh token
// * GET  /api/v1/auth/me           — return authenticated user profile
// *
// * TODO: Inject OtpService, JwtUtils, UserService and implement.
// */
//@RestController
//@RequestMapping("/api/v1/auth")
//public class AuthController {
//
//    @PostMapping("/otp/request")
//    public ResponseEntity<Map<String, String>> requestOtp(@RequestBody Map<String, String> body) {
//        // TODO: validate phoneNumber, call OtpService.sendOtp(phoneNumber)
//        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
//    }
//
//    @PostMapping("/otp/verify")
//    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
//        // TODO: OtpService.verify(phoneNumber, code) → load user → generate JWT
//        return ResponseEntity.ok(Map.of("message", "TODO: return accessToken, refreshToken, user"));
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
//        // TODO: validate refreshToken → issue new accessToken
//        return ResponseEntity.ok(Map.of("message", "TODO: return new accessToken"));
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<Map<String, String>> me() {
//        // TODO: return authenticated user from SecurityContext
//        return ResponseEntity.ok(Map.of("message", "TODO: return user profile"));
//    }
//}

package com.umudugudu.controller;

import com.umudugudu.dto.response.AuthResponse;
import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestBody Map<String, String> body) {

        String phoneNumber = body.get("phoneNumber");

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "phoneNumber is required"));
        }

        authService.sendOtp(phoneNumber);

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {

        String phoneNumber = body.get("phoneNumber");
        String code = body.get("code");

        if (phoneNumber == null || code == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "phoneNumber and code are required"));
        }

        AuthResponse response = authService.verifyOtp(phoneNumber, code);

        return ResponseEntity.ok(Map.of(
                "accessToken", response.getAccessToken(),
                "refreshToken", response.getRefreshToken(),
                "user", response.getUser(),
                "message", response.getMessage()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "refreshToken is required"));
        }

        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", response.getAccessToken(),
                "message", response.getMessage()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
        }

        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                        userRepository.findByPhoneNumber(username)
                                .orElseThrow(() -> new RuntimeException("User not found"))
                );

        return ResponseEntity.ok(Map.of(
                "user", user
        ));
    }
}