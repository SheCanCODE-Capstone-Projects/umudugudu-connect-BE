package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private String performedBy;
    private String action;
    private String entityType;
    private UUID entityId;
    private String oldValue;
    private String newValue;
    private LocalDateTime performedAt;
}
