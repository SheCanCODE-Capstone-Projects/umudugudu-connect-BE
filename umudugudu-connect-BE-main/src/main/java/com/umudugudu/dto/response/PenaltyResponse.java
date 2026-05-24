package com.umudugudu.dto.response;

import com.umudugudu.entity.PenaltyStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PenaltyResponse {

    private UUID id;
    private UUID activityId;
    private String activityTitle;
    private UUID citizenId;
    private String citizenFullName;
    private PenaltyStatus status;
    private BigDecimal amount;
    private String reason;
    private LocalDate dueDate;
    private LocalDateTime paidAt;
    private String exemptionReason;
    private String reviewedByFullName;
    private LocalDateTime flaggedAt;
    private LocalDateTime reviewedAt;
}