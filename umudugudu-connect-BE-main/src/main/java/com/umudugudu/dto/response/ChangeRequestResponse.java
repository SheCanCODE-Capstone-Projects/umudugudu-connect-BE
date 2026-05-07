package com.umudugudu.dto.response;

import com.umudugudu.entity.ChangeRequestStatus;
import com.umudugudu.entity.ProfileChangeRequest;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ChangeRequestResponse {
    private final Long id;
    private final String requestedFirstName;
    private final String requestedLastName;
    private final String requestedPhoneNumber;
    private final String requestedVillage;
    private final String requestedIsibo;
    private final ChangeRequestStatus status;
    private final LocalDateTime createdAt;

    public ChangeRequestResponse(ProfileChangeRequest req) {
        this.id                   = req.getId();
        this.requestedFirstName   = req.getRequestedFirstName();
        this.requestedLastName    = req.getRequestedLastName();
        this.requestedPhoneNumber = req.getRequestedPhoneNumber();
        this.requestedVillage     = req.getRequestedVillage();
        this.requestedIsibo       = req.getRequestedIsibo();
        this.status               = req.getStatus();
        this.createdAt            = req.getCreatedAt();
    }
}