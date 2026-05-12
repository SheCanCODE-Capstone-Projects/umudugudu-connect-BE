package com.umudugudu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String message;
    private UserDto user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String role;
        private boolean enabled;
        private boolean verified;
        private UUID isiboId;
        private String isiboName;
        private UUID villageId;
        private String villageName;
    }
}