package com.umudugudu.controller;

import com.umudugudu.dto.request.NotificationPreferenceRequest;
import com.umudugudu.entity.NotificationPreference;
import com.umudugudu.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {
    private final NotificationPreferenceService preferenceService;


    @GetMapping
    public NotificationPreference getMyPreferences() {
        return preferenceService.getMyPreferences();
    }


    @PutMapping
    public NotificationPreference updatePreferences(
            @RequestBody NotificationPreferenceRequest request
    ) {
        return preferenceService.updatePreferences(request);
    }
}
