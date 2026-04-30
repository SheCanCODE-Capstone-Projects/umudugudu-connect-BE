package com.umudugudu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String phoneNumber;
}