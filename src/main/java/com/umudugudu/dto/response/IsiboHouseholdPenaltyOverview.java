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
public class IsiboHouseholdPenaltyOverview {

    private UUID isiboId;
    private String isiboName;
    private long totalHouseholdsWithPenalties;
    private long totalUnpaidPenalties;
    private BigDecimal totalOutstandingAmount;
    private List<HouseholdPenaltySummary> households;

}

