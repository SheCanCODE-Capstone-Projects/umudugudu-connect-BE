package com.umudugudu.service;

import com.umudugudu.entity.Activity;
import com.umudugudu.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService  {
    void notifyVillage(Activity activity);

    void sendPushNotification(Activity activity);

    void sendSmsNotification(Activity activity);
    void notifyCitizen(
            User citizen,
            String message
    );

    void sendSms(String phoneNumber, String message);
}
