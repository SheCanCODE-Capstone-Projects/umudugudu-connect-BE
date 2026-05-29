package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class AnnouncementResponse {
    private UUID id;

    private String title;

    private String message;

    private String status;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;
}
