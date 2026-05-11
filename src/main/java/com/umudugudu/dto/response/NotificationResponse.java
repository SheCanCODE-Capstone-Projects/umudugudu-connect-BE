package com.umudugudu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String body;
    private String deepLink;
    private boolean read;
    private LocalDateTime createdAt;
}
