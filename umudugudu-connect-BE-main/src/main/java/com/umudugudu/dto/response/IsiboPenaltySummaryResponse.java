package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class IsiboPenaltySummaryResponse {

    private UUID isiboId;
    private String isiboName;
    private int totalHouseholds;
    private int householdsWithUnpaid;
    private BigDecimal totalOutstandingAmount;
    private int totalUnpaidCount;

    private List<HouseholdPenaltySummary> households;

    @Data
    @Builder
    public static class HouseholdPenaltySummary {
        private UUID citizenId;
        private String citizenFullName;
        private int unpaidCount;
        private BigDecimal totalOutstanding;
        private List<PenaltyResponse> penalties;
    }
}