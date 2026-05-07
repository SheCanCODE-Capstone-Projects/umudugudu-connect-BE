package com.umudugudu.dto.request;

import lombok.Data;

@Data
public class ReviewChangeRequestDTO {
    private boolean approved;
    private String rejectionReason;
}
