package com.umudugudu.dto.response;

import com.umudugudu.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private String email;
    private Role role;
}
