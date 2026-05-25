package com.umudugudu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "exemptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Exemption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    private UUID activityId;

    @ManyToOne
    private User citizen;

    private String reason;

    private boolean approved;
}
