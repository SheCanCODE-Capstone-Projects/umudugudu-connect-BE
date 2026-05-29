package com.umudugudu.service.impl;

import com.umudugudu.dto.request.NotificationPreferenceRequest;
import com.umudugudu.entity.NotificationPreference;
import com.umudugudu.entity.User;
import com.umudugudu.repository.NotificationPreferenceRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepo;
    private final UserRepository userRepo;

    @Override
    public NotificationPreference getMyPreferences() {
        User user = getCurrentUser();

        return preferenceRepo.findByUser(user)
                .orElseGet(() -> preferenceRepo.save(
                        createDefault(user)
                ));
    }

    @Override
    public NotificationPreference updatePreferences(NotificationPreferenceRequest request) {
        User user = getCurrentUser();

        NotificationPreference pref = preferenceRepo.findByUser(user)
                .orElse(createDefault(user));

        if (request.getActivityReminders() != null) {
            pref.setActivityReminders(request.getActivityReminders());
        }

        if (request.getAnnouncements() != null) {
            pref.setAnnouncements(request.getAnnouncements());
        }

        if (request.getPenalties() != null) {
            pref.setPenalties(request.getPenalties());
        }

        return preferenceRepo.save(pref);
    }

    private NotificationPreference createDefault(User user) {
        NotificationPreference pref = new NotificationPreference();
        pref.setUser(user);
        pref.setActivityReminders(true);
        pref.setAnnouncements(true);
        pref.setPenalties(true);
        return pref;
    }

    private User getCurrentUser() {
        return userRepo.findAll().get(0);
    }
}