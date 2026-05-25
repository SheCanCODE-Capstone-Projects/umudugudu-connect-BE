package com.umudugudu.dto.request;

<<<<<<< HEAD
=======
import com.umudugudu.entity.PenaltyPaymentStatus;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
import com.umudugudu.entity.PenaltyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewPenaltyRequest {

    @NotNull(message = "Decision is required")
    private PenaltyStatus decision; // CONFIRMED | WAIVED

    private String reviewNote;
}