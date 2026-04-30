package com.umudugudu.service;

import com.umudugudu.dto.request.LoginRequest;
import com.umudugudu.dto.request.RegisterRequest;
import com.umudugudu.dto.response.AuthResponse;

public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void sendOtp(String phone);

    AuthResponse verifyOtp(String phone, String code);

    AuthResponse refreshToken(String refreshToken);
}