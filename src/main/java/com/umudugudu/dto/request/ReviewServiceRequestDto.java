package com.umudugudu.dto.request;

import com.umudugudu.entity.ServiceRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewServiceRequestDto {

    /**
     * Must be one of: APPROVED, REJECTED, INFO_REQUIRED
     */
    @NotNull(message = "Decision is required")
    private ServiceRequestStatus decision;

    /**
     * Required when decision is REJECTED or INFO_REQUIRED.
     * Optional but recommended for APPROVED.
     */
    private String leaderResponse;
}
