package com.umudugudu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateIsiboRequest {

    @NotBlank(message = "Isibo name is required")
    private String name;

    private UUID isiboLeaderId;
}
