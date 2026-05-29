package com.umudugudu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseholdPenaltySummary {

    private UUID householdId;
    private String householdHeadName;
    private String phoneNumber;
    private long totalUnpaidPenalties;
    private BigDecimal totalOutstandingAmount;
    private List<PenaltyFlagResponse> penaltyDetails;

}

