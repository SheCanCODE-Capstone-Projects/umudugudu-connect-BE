package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue
    private UUID id;

    // Village that owns this activity
    @Column(name = "village_id", nullable = false)
    private UUID villageId;

    // Village leader who created it
    @Column(name = "created_by")
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "scheduled_at", nullable = false)
    private ZonedDateTime scheduledAt;

    @Column(nullable = false, length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.SCHEDULED;
}
