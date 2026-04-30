package com.umudugudu.service;

import com.umudugudu.dto.response.AuthResponse;

public interface AuthService {

    void sendOtp(String phone);

    AuthResponse verifyOtp(String phone, String code);

    AuthResponse refreshToken(String refreshToken);
}

