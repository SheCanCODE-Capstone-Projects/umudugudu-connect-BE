package com.umudugudu.dto.response;

import com.umudugudu.entity.ChangeRequestStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChangeRequestResponse {
    private UUID                id;
    private UUID                userId;
    private String              userFullName;
    private String              requestedFullName;
    private String              requestedEmail;
    private UUID                requestedVillageId;
    private UUID                requestedIsiboId;
    private String              citizenNote;
    private ChangeRequestStatus status;
    private String              leaderResponse;
    private LocalDateTime       createdAt;
    private LocalDateTime       reviewedAt;
}
