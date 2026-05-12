package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"activity_id", "citizen_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "activity_id", nullable = false)
    private UUID activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by_id", nullable = false)
    private User markedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status; // PRESENT | ABSENT

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    @Column(name = "synced_from_offline", nullable = false)
    private boolean syncedFromOffline = false;

    @Column(name = "offline_marked_at")
    private LocalDateTime offlineMarkedAt;

    @PrePersist
    public void prePersist() {
        if (this.markedAt == null) {
            this.markedAt = LocalDateTime.now();
        }
    }
}
