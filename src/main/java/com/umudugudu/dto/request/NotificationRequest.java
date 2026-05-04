package com.umudugudu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String message;
    private UUID targetIsibo;
    private UUID targetUserId;
    private boolean sendSmsIfNoInternet = true;
}

