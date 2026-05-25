package com.umudugudu.dto.request;

import com.umudugudu.entity.ServiceRequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitServiceRequestDto {

    @NotNull(message = "Request type is required")
    private ServiceRequestType requestType;

    @NotBlank(message = "Description is required")
    private String description;
}
