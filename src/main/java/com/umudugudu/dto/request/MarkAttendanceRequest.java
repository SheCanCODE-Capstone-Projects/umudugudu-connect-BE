package com.umudugudu.dto.request;

import com.umudugudu.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MarkAttendanceRequest {

    @NotNull(message = "Citizen ID is required")
    private Long citizenId;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    private LocalDateTime offlineMarkedAt;

    private boolean syncedFromOffline = false;
}