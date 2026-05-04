package com.umudugudu.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ChangeRequestSubmitRequest {
    private String fullName;

    @Email
    private String email;

    private UUID   villageId;
    private UUID   isiboId;
    private String note;
}