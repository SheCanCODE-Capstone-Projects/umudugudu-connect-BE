package com.umudugudu.service;

import com.umudugudu.dto.request.LoginRequest;
import com.umudugudu.dto.request.PhoneLoginRequest;
import com.umudugudu.dto.request.RegisterRequest;
import com.umudugudu.dto.response.AuthResponse;

public interface AuthService {

    void sendOtp(String phone);
    void sendOtpToEmail(String email);
    String verifyEmailOtp(String email, String code);
    AuthResponse verifyOtp(String phone, String code);

    AuthResponse refreshToken(String refreshToken);
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void resendEmailOtp(String email);
    AuthResponse loginWithPhone(PhoneLoginRequest request);
    AuthResponse verifyLoginOtp(String email, String code);

}

