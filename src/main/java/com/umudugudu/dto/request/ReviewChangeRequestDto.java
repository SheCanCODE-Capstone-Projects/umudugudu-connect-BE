package com.umudugudu.dto.request;

import lombok.Data;

@Data
public class ReviewChangeRequestDto {
    private boolean approved;
    private String rejectionReason;
}