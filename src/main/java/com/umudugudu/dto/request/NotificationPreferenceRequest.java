package com.umudugudu.dto.request;

import lombok.Data;

@Data
public class NotificationPreferenceRequest {
    private Boolean activityReminders;
    private Boolean announcements;
    private Boolean penalties;
}
