package com.umudugudu.dto.response;

import lombok.*;

import java.util.UUID;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
