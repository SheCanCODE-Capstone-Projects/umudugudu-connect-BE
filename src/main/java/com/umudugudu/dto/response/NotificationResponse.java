package com.umudugudu.dto.response;

import com.umudugudu.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID id;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private UUID recipientId;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .recipientId(notification.getRecipient().getId())
                .build();
    }
}