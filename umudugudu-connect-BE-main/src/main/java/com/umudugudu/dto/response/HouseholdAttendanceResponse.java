
package com.umudugudu.dto.response;

import com.umudugudu.entity.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HouseholdAttendanceResponse {

    private UUID citizenId;
    private String citizenFullName;
    private AttendanceStatus status;
    private LocalDateTime markedAt;
    private boolean syncedFromOffline;
}