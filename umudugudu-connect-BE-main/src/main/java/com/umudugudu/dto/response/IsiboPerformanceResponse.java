
package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class IsiboPerformanceResponse {

    private UUID isiboId;
    private String isiboName;

    private int    totalInvited;
    private int    totalPresent;
    private int    totalAbsent;
    private double participationPercentage;

    private List<HouseholdAttendanceResponse> householdAttendance;
}