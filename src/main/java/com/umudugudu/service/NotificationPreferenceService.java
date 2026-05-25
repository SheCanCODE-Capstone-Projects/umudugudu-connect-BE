package com.umudugudu.service;

import com.umudugudu.dto.request.NotificationPreferenceRequest;
import com.umudugudu.entity.NotificationPreference;

public interface NotificationPreferenceService {
    NotificationPreference getMyPreferences();

    NotificationPreference updatePreferences(NotificationPreferenceRequest request);
}
