package com.umudugudu.dto;

import com.umudugudu.model.User;
import com.umudugudu.model.UserRole;

import java.util.UUID;

public record UserProfileDto(
    UUID     id,
    String   fullName,
    String   phoneNumber,
    UserRole role,
    UUID     villageId,
    String   villageName,
    UUID     isibId,
    String   isibName,
    boolean  isActive
) {
    public static UserProfileDto from(User user) {
        return new UserProfileDto(
            user.getId(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getRole(),
            user.getVillage() != null ? user.getVillage().getId()   : null,
            user.getVillage() != null ? user.getVillage().getName() : null,
            user.getIsib()    != null ? user.getIsib().getId()      : null,
            user.getIsib()    != null ? user.getIsib().getName()    : null,
            user.isEnabled()
        );
    }
}
