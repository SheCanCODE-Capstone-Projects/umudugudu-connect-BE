package com.umudugudu.dto.response;

import com.umudugudu.entity.PenaltyPaymentStatus;
import com.umudugudu.entity.PenaltyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyFlagResponse {

    private UUID id;
    private UUID activityId;
    private UUID citizenId;
    private String citizenFullName;
    private UUID attendanceId;
    private PenaltyStatus status;
    private PenaltyPaymentStatus paymentStatus;
    private String reviewNote;
    private String reviewedByFullName;
    private LocalDateTime flaggedAt;
    private LocalDateTime reviewedAt;
}