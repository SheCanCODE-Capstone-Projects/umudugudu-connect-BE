package com.umudugudu.dto.request;

import lombok.Data;

import java.util.UUID;
@Data
public class CreateEmergencyRequest {
    private String type;

    private String description;

    private Double latitude;

    private Double longitude;

    private UUID villageId;
}
