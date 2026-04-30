package com.umudugudu.service.impl;

import com.umudugudu.dto.request.LoginRequest;
import com.umudugudu.dto.request.RegisterRequest;
import com.umudugudu.dto.response.AuthResponse;
import com.umudugudu.entity.Otp;
import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.repository.OtpRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.security.JwtUtils;
import com.umudugudu.service.AuthService;
import com.umudugudu.util.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final SmsService smsService;

    @Override
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.CITIZEN)
                .enabled(true)
                .build();

        userRepository.save(user);

        return "Registered Successfully";
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtils.generateToken(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Login successful",
                user
        );
    }

    // ================= SEND OTP =================
    @Override
    public void sendOtp(String phone) {

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        Otp otp = new Otp();
        otp.setPhoneNumber(phone);
        otp.setCode(code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        smsService.sendSms(phone, "Your OTP is " + code);
    }

    // ================= VERIFY OTP =================
    @Override
    public AuthResponse verifyOtp(String phone, String code) {

        Otp otp = otpRepository.findTopByPhoneNumberOrderByIdDesc(phone)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otp.getCode().equals(code)) {
            throw new RuntimeException("Invalid OTP");
        }

        // find user (or create if not exists)
        User user = userRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .phoneNumber(phone)
                            .role(Role.CITIZEN)
                            .enabled(true)
                            .build();
                    return userRepository.save(newUser);
                });

        String accessToken = jwtUtils.generateToken(user.getEmail() != null ? user.getEmail() : phone);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail() != null ? user.getEmail() : phone);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "OTP verified successfully",
                user
        );
    }

    // ================= REFRESH TOKEN =================
    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String username = jwtUtils.extractUsername(refreshToken);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtils.isTokenValid(refreshToken, username)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtUtils.generateToken(username);

        return new AuthResponse(
                newAccessToken,
                refreshToken,
                "Token refreshed successfully",
                user
        );
    }
}