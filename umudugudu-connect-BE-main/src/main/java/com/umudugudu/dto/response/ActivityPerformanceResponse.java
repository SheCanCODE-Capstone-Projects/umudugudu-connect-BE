
package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ActivityPerformanceResponse {

    private UUID   activityId;
    private String title;
    private String type;
    private String status;
    private ZonedDateTime scheduledAt;
    private String location;

    private int    totalInvited;
    private int    totalPresent;
    private int    totalAbsent;
    private double participationPercentage;

    private List<IsiboPerformanceResponse> isiboBreakdown;
}