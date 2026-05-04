package com.umudugudu.dto.request;

import com.umudugudu.entity.ChangeRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRequestReviewRequest {
    @NotNull
    private ChangeRequestStatus decision; // APPROVED or REJECTED

    private String response; // leader's note back to the citizen
}