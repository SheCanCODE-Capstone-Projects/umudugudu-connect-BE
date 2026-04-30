package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String email;
    private String password;
    private String role;
    private boolean enabled;
}