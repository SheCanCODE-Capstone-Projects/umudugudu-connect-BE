package com.umudugudu.dto.response;

import com.umudugudu.entity.Role;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String fullName;
    private Role   role;
}
