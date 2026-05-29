package com.umudugudu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ExemptionResponse {
    private UUID id;
    private UUID attendanceId;
    private String reason;
    private String status;
}
