package com.umudugudu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;


    @Column(unique = true, nullable = false)
    @NotBlank
    private String email;

    @Column(unique = true)
    private String phoneNumber;

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


    private boolean smsNotificationsEnabled = true;

    private String preferredNotificationMethod = "FCM";

    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isibos", "villageLeader"})
    private Village village;


}