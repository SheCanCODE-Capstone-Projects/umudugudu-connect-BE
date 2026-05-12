package com.umudugudu.Mapper;

import com.umudugudu.dto.response.AuthResponse.UserDto;
import com.umudugudu.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .enabled(user.isEnabled())
                .verified(user.isVerified())
                .isiboId(user.getIsibo() != null ? user.getIsibo().getId() : null)
                .isiboName(user.getIsibo() != null ? user.getIsibo().getName() : null)
                .villageId(user.getVillage() != null ? user.getVillage().getId() : null)
                .villageName(user.getVillage() != null ? user.getVillage().getName() : null)
                .build();
    }
}