package com.umudugudu.service.impl;

import com.umudugudu.entity.Activity;
import com.umudugudu.entity.NotificationPreference;
import com.umudugudu.entity.NotificationType;
import com.umudugudu.entity.User;
import com.umudugudu.repository.NotificationPreferenceRepository;
import com.umudugudu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    @Override
    public void notifyVillage(Activity activity) {
        log.info("Sending notifications for activity: {}", activity.getTitle());
    }

    @Override
    public void sendPushNotification(Activity activity) {
        log.info("Sending PUSH notification: {} - {} at {}",
                activity.getTitle(),
                activity.getScheduledAt(),
                activity.getLocation());
    }

    @Override
    public void sendSmsNotification(Activity activity) {
        log.info("Sending SMS: {} - {} at {}",
                activity.getTitle(),
                activity.getScheduledAt(),
                activity.getLocation());
    }

    @Override
    public void notifyCitizen(User citizen, String message) {
        // 1. log notification
        log.info("Notifying citizen {}: {}", citizen.getId(), message);

        // 2. send SMS
        if (citizen.getPhoneNumber() != null) {
            sendSms(citizen.getPhoneNumber(), message);
        }

    }

    @Override
    public void sendSms(String phoneNumber, String message){
        System.out.println("SMS to " + phoneNumber + ": " + message);
    }


    @Override
    public void sendToUsers(List<User> users, String title, String message) {

        log.info("Sending PUSH + SMS notifications...");

        for (User user : users) {

            // PUSH (mock for now)
            log.info("PUSH to {}: {}", user.getId(), message);

            // SMS fallback
            if (user.getPhoneNumber() != null) {
                sendSms(user.getPhoneNumber(), message);
            }
        }
    }

    @Override
    public boolean shouldSendNotification(User user, NotificationType type) {
        if (type == NotificationType.EMERGENCY) {
            return true;
        }

        NotificationPreference pref = notificationPreferenceRepository.findByUser(user)
                .orElse(getDefaultPreference(user));

        return switch (type) {
            case ACTIVITY_REMINDER -> pref.isActivityReminders();
            case ANNOUNCEMENT -> pref.isAnnouncements();
            case PENALTY -> pref.isPenalties();
            default -> true;
        };
    }

    private NotificationPreference getDefaultPreference(User user) {
        return NotificationPreference.builder()
                .user(user)
                .activityReminders(true)
                .announcements(true)
                .penalties(true)
                .build();
    }
}

