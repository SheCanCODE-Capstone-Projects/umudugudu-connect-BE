package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChangeRequestResponse {
    private UUID id;
    private String requesterName;
    private String requestedFirstName;
    private String requestedLastName;
    private String requestedPhoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String rejectionReason;
}
