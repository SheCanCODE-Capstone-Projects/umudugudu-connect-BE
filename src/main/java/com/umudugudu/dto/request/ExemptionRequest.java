package com.umudugudu.dto.request;

import lombok.Data;

import java.util.UUID;
@Data
public class ExemptionRequest {
    private UUID attendanceId;
    private String reason;
}
