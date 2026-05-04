package com.umudugudu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;
    @Column(unique = true, nullable = false)
    @NotBlank
   private String email;;

    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    private UUID villageId;

    private UUID isiboId;

    private boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
