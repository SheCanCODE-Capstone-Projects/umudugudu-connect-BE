package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.*;
import com.umudugudu.entity.TargetType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID villageId;

    private UUID createdBy;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private AnnouncementStatus status;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;
}
