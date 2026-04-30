package com.umudugudu.dto;

import com.umudugudu.model.UserRole;

import java.util.UUID;

public record AuthResponseDto(
    String accessToken,
    String refreshToken,
    UserProfile user
) {
    public record UserProfile(
        UUID   id,
        String fullName,
        String phoneNumber,
        UserRole role,
        UUID   villageId,
        UUID   isibId
    ) {}
}
