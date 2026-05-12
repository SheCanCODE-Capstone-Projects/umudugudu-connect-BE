package com.umudugudu.dto.request;

import com.umudugudu.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MarkAttendanceRequest {

    @NotNull(message = "Citizen ID is required")
    private UUID citizenId;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    private LocalDateTime offlineMarkedAt;

    private boolean syncedFromOffline = false;
}