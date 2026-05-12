package com.umudugudu.dto.request;

import com.umudugudu.entity.PenaltyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewPenaltyRequest {

    @NotNull(message = "Decision is required")
    private PenaltyStatus decision; // CONFIRMED or WAIVED

    private String reviewNote;
}