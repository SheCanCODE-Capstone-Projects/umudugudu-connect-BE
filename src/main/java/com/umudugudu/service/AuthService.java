package com.umudugudu.service;

import com.umudugudu.dto.request.LoginRequest;
import com.umudugudu.dto.request.RegisterRequest;
import com.umudugudu.dto.response.AuthResponse;

public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String sendOtp(String phone);

    String verifyOtp(String phone,String code);
}
