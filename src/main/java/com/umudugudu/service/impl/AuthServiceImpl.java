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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final SmsService smsService;
    private final EmailService emailService;
    @Override

    public void sendOtp(String phone) {

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        Otp otp = new Otp();
        otp.setEmail(phone);
        otp.setCode(code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setAttempts(0);

        otpRepository.save(otp);

        smsService.sendSms(phone, "Your OTP is " + code);
    }

    @Override
    public void sendOtpToEmail(String email) {

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setAttempts(0);

        otpRepository.save(otp);

        emailService.sendOtpEmail(email, code);
    }

    @Override
    public AuthResponse verifyOtp(String phone, String code) {

        Otp otp = otpRepository.findTopByPhoneNumberOrderByIdDesc(phone)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            throw new RuntimeException("Code expired");
        }

        if (!otp.getCode().equals(code)) {

            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);

            int remaining = 3 - otp.getAttempts();

            if (remaining <= 0) {
                throw new RuntimeException("Too many attempts. Request new OTP");
            }

            throw new RuntimeException("Invalid code — " + remaining + " attempts remaining");
        }

        User user = userRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhoneNumber(phone);
                    newUser.setRole(Role.CITIZEN);
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });

        String username = user.getEmail() != null
                ? user.getEmail()
                : user.getPhoneNumber();

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, "OTP verified", user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String username = jwtUtils.extractUsername(refreshToken);

        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                "",
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                ))
        );

        if (!jwtUtils.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        return new AuthResponse(newAccessToken, refreshToken, "Token refreshed", user);
    }
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode(request.getPassword()));
        user.setRole(Role.CITIZEN);

        user.setEnabled(false);
        user.setVerified(false);

        userRepository.save(user);

        sendOtpToEmail(user.getEmail());

        return new AuthResponse(
                null,
                null,
                "OTP sent to email. Please verify before login.",
                user
        );
    }
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                ))
        );

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, "Login successful", user);
    }
}
