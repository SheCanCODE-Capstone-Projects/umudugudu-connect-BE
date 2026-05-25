package com.umudugudu.service.impl;

import com.umudugudu.entity.Activity;
import com.umudugudu.entity.User;
import com.umudugudu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
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
    }

