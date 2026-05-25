package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergency_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyReport {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID citizenId;

    private UUID villageId;

    private String type; // FIRE, FLOOD, HEALTH

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double latitude;

    private Double longitude;

    private LocalDateTime createdAt;

    private boolean resolved;
}
