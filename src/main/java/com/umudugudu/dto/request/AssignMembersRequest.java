package com.umudugudu.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class AssignMembersRequest {

    @NotEmpty(message = "At least one member ID is required")
    private List<Long> memberIds;
}