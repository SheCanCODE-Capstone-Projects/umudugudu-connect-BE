package com.umudugudu.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
=======
import jakarta.validation.constraints.NotNull;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "penalty_flags",
        uniqueConstraints = {
<<<<<<< HEAD
                @UniqueConstraint(columnNames = {"activity_id", "citizen_id"})
=======
                @UniqueConstraint(
                        columnNames = {
                                "activity_id",
                                "citizen_id"
                        }
                )
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
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
<<<<<<< HEAD
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyStatus status; // starts as FLAGGED via @PrePersist
=======
    @JoinColumn(
            name = "citizen_id",
            nullable = false
    )
    private User citizen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "attendance_id",
            nullable = false
    )
    private Attendance attendance;

    // FLAGGED / APPROVED / WAIVED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyStatus reviewStatus;

    // UNPAID / PAID
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyPaymentStatus paymentStatus;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)

    private String reviewNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

<<<<<<< HEAD
    @Column(name = "flagged_at", nullable = false)
=======
    @Column(
            name = "flagged_at",
            nullable = false
    )
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    private LocalDateTime flaggedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
<<<<<<< HEAD
        this.flaggedAt = LocalDateTime.now();
        this.status = PenaltyStatus.FLAGGED;
=======

        if (flaggedAt == null) {
            flaggedAt = LocalDateTime.now();
        }

        if (reviewStatus== null) {
            reviewStatus = PenaltyStatus.FLAGGED;
        }

        if (paymentStatus == null) {
            paymentStatus =
                    PenaltyPaymentStatus.UNPAID;
        }
    }

    public void setReviewStatus(@NotNull(message = "Decision is required") PenaltyStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    }
}