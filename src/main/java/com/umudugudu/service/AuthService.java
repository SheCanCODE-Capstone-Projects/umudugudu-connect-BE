package com.umudugudu.service;

public interface AuthService {

    void sendOtp(String phone);

    AuthResponse verifyOtp(String phone, String code);

    AuthResponse refreshToken(String refreshToken);
}

