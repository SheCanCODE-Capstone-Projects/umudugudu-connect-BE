package com.umudugudu.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityRequest {

    @NotBlank(message = "Activity name is required")
    private String name;

    private String description;

    @NotNull(message = "Activity date is required")
    @Future(message = "Activity date must be in the future")
    private LocalDateTime activityDate;

    @NotBlank(message = "Location is required")
    private String location;
}
