package com.umudugudu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;
    private boolean verified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isibo_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "citizens", "village", "isiboLeader"})
    private Isibo isibo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isibos", "villageLeader"})
    private Village village;
}