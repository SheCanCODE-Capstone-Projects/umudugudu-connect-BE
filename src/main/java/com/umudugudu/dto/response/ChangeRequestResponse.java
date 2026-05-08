package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ChangeRequestResponse {
    private Long id;
    private String requesterName;
    private String requestedFirstName;
    private String requestedLastName;
    private String requestedPhoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String rejectionReason;
}
