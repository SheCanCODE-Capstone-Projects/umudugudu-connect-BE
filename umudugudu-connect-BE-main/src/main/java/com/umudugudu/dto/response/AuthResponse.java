package com.umudugudu.dto.response;

import com.umudugudu.entity.User;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String message;
    private User user;

    public AuthResponse(String accessToken, String refreshToken, String message, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
}