package com.umudugudu.dto.response;

import com.umudugudu.entity.AttendanceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AttendanceResponse {

    private UUID id;
    private UUID activityId;
    private UUID citizenId;
    private String citizenFullName;
    private String markedByFullName;
    private AttendanceStatus status;
    private LocalDateTime markedAt;
    private boolean syncedFromOffline;
    private LocalDateTime offlineMarkedAt;
}