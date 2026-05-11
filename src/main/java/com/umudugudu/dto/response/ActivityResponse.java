package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime activityDate;
    private String location;
    private String createdByName;
    private LocalDateTime createdAt;
}
