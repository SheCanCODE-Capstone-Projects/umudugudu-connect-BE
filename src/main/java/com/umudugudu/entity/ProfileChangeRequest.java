package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "profile_change_requests")
public class ProfileChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The citizen who requested the change
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    // Fields the citizen wants to update
    private String requestedFirstName;
    private String requestedLastName;
    private String requestedPhoneNumber;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }
}