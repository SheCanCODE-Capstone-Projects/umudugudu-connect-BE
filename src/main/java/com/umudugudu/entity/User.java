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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isibos", "villageLeader"})
    private Village village;

    @Column(name = "sms_notifications_enabled", nullable = false,
            columnDefinition = "boolean default true")
    private boolean smsNotificationsEnabled = true;

    @Column(name = "preferred_notification_method",
            columnDefinition = "varchar(255) default 'FCM'")
    private String preferredNotificationMethod = "FCM";

    @Column(name = "is_active", nullable = false,
            columnDefinition = "boolean default true")
    private boolean isActive = true;
}