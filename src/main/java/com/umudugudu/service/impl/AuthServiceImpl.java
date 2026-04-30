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
                .orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token,"Login Success");
    }

    @Override
    public String sendOtp(String phone) {

        String code = String.valueOf(new Random().nextInt(900000)+100000);

        Otp otp = new Otp();
        otp.setPhoneNumber(phone);
        otp.setCode(code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        smsService.sendSms(phone,"Your OTP is "+code);

        return "OTP Sent";
    }

    @Override
    public String verifyOtp(String phone, String code) {

        Otp otp = otpRepository.findTopByPhoneNumberOrderByIdDesc(phone)
                .orElseThrow();

        if(LocalDateTime.now().isAfter(otp.getExpiryTime()))
            return "OTP Expired";

        if(!otp.getCode().equals(code))
            return "Invalid OTP";

        return "Verified";
    }
}
