package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalVillages;
    private long totalActivities;
    private long totalPenalties;
    private long confirmedPenalties;
    private long waivedPenalties;
    private long flaggedPenalties;
    private long totalAuditLogs;
}