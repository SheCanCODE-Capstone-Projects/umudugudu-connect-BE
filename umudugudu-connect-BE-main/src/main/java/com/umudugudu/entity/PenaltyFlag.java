package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "penalty_flags",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"activity_id", "citizen_id"})
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyStatus status;

    private String reviewNote;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    private String reason;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "exemption_reason")
    private String exemptionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "flagged_at", nullable = false)
    private LocalDateTime flaggedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        this.flaggedAt = LocalDateTime.now();
        this.status = PenaltyStatus.FLAGGED;
    }
}