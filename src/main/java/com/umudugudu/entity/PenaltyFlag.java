package com.umudugudu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "penalty_flags",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "activity_id",
                                "citizen_id"
                        }
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PenaltyFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "activity_id", nullable = false)
    private UUID activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    // FLAGGED / CONFIRMED / WAIVED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyStatus reviewStatus;

    // UNPAID / PAID
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyPaymentStatus paymentStatus;

    private String reviewNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "flagged_at", nullable = false)
    private LocalDateTime flaggedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        if (flaggedAt == null) {
            flaggedAt = LocalDateTime.now();
        }
        if (reviewStatus == null) {
            reviewStatus = PenaltyStatus.FLAGGED;
        }
        if (paymentStatus == null) {
            paymentStatus = PenaltyPaymentStatus.UNPAID;
        }
    }
}