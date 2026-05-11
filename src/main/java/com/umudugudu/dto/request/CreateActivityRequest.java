package com.umudugudu.dto.request;

import com.umudugudu.entity.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;
@Data
public class CreateActivityRequest {
    @NotNull
    private UUID villageId;

    @NotBlank
    private String title;

    @NotNull
    private ZonedDateTime scheduledAt;

    @NotBlank
    private String location;

    @NotNull
    private ActivityType type;
}
