//package com.umudugudu.security;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * TODO: Inject UserRepository and load real User entity.
// * This stub compiles and lets Spring Boot start cleanly.
// */
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Override
//    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
//        // TODO: replace with:
//        //   User user = userRepository.findByPhoneNumber(phoneNumber)
//        //       .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phoneNumber));
//        //   return new org.springframework.security.core.userdetails.User(
//        //       user.getPhoneNumber(), "", List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
//        throw new UsernameNotFoundException("UserRepository not yet wired — implement this service.");
//    }
//}
package com.umudugudu.security;

import com.umudugudu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestBody Map<String, String> body) {

        String phoneNumber = body.get("phoneNumber");

        authService.sendOtp(phoneNumber);

        return ResponseEntity.ok(
                Map.of("message", "OTP sent successfully")
        );
    }
    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {

        String phoneNumber = body.get("phoneNumber");
        String code = body.get("code");

        Map<String, Object> response =
                authService.verifyOtpAndLogin(phoneNumber, code);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refreshToken");

        Map<String, String> response =
                authService.refreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/me")
    public ResponseEntity<?> me() {

        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
