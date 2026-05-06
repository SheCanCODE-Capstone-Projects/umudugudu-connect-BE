package com.umudugudu.service;

import com.umudugudu.dto.request.LoginRequest;
import com.umudugudu.dto.request.RegisterRequest;
import com.umudugudu.dto.response.AuthResponse;

public interface AuthService {

    void sendOtp(String phone);
    void sendOtpToEmail(String email);
    AuthResponse verifyOtp(String phone, String code);

    AuthResponse refreshToken(String refreshToken);
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

}

