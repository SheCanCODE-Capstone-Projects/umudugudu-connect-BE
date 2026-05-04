package com.umudugudu.dto.response;

import com.umudugudu.entity.Role;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class ProfileResponse {
    private UUID    id;
    private String  fullName;
    private String  email;
    private Role    role;
    private UUID    villageId;
    private UUID    isiboId;
    private boolean active;
}
