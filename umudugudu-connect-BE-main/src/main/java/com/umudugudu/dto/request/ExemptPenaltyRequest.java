package com.umudugudu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExemptPenaltyRequest {

    @NotBlank(message = "Exemption reason is required")
    private String exemptionReason;
}