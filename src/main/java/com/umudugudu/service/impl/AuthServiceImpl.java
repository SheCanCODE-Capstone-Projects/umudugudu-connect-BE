package com.umudugudu.service.impl;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final JwtUtils jwtUtils;
    private final SmsService smsService;


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

        User user = userRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .phoneNumber(phone)
                            .role(Role.CITIZEN)
                            .enabled(true)
                            .build();
                    return userRepository.save(newUser);
                });

        UserDetails userDetails = buildUserDetails(user);

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, "OTP verified", user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String username = jwtUtils.extractUsername(refreshToken);

        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                        userRepository.findByPhoneNumber(username)
                                .orElseThrow(() -> new RuntimeException("User not found"))
                );

        UserDetails userDetails = buildUserDetails(user);

        if (!jwtUtils.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        return new AuthResponse(newAccessToken, refreshToken, "Token refreshed", user);
    }

    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(),
                "",
                Collections.emptyList()
        );
    }
}