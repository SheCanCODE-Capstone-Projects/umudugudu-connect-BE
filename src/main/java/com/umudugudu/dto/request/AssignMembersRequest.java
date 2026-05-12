package com.umudugudu.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AssignMembersRequest {

    @NotEmpty(message = "At least one member ID is required")
    private List<UUID> memberIds;
}