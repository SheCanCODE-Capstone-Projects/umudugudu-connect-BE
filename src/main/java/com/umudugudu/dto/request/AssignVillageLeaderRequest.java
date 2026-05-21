package com.umudugudu.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class AssignVillageLeaderRequest {
    private String email;
    private UUID villageId;
}