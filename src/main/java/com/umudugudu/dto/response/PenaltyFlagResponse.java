package com.umudugudu.dto.response;

import com.umudugudu.entity.PenaltyStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PenaltyFlagResponse {

    private Long id;
    private UUID activityId;
    private Long citizenId;
    private String citizenFullName;
    private Long attendanceId;
    private PenaltyStatus status;
    private String reviewNote;
    private String reviewedByFullName;
    private LocalDateTime flaggedAt;
    private LocalDateTime reviewedAt;
}