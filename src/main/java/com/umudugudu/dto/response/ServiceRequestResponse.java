package com.umudugudu.dto.response;

import com.umudugudu.entity.ServiceRequestStatus;
import com.umudugudu.entity.ServiceRequestType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ServiceRequestResponse {

    private UUID id;

    // Citizen info
    private UUID citizenId;
    private String citizenFullName;
    private String citizenPhone;

    // Request details
    private ServiceRequestType requestType;
    private String description;
    private ServiceRequestStatus status;

    // Leader review
    private String leaderResponse;
    private String reviewedByFullName;
    private LocalDateTime reviewedAt;

    // Timestamps
    private LocalDateTime createdAt;

    // Location context
    private UUID villageId;
    private UUID isiboId;
}
