package com.umudugudu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateAnnouncementRequest {
    private String title;

    private String message;

    private LocalDateTime scheduledAt;


    private List<UUID> isibIds;
    private UUID villageId;
}
