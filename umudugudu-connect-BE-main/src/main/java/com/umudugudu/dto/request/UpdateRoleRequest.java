package com.umudugudu.dto.request;

import com.umudugudu.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {
    @NotBlank
    private String email;

    @NotNull
    private Role role;
}
