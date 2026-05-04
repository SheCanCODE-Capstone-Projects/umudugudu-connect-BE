package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sms_logs")
public class SmsLog {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private SmsStatus status;

    private String provider;

    private String externalReference;

    private LocalDateTime sentAt;

    private LocalDateTime deliveredAt;

    private String errorMessage;

    public enum SmsStatus {
        PENDING, SENT, DELIVERED, FAILED
    }

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}
