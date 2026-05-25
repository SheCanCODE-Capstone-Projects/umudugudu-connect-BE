package com.umudugudu.service;

import com.umudugudu.entity.Activity;
import com.umudugudu.entity.NotificationType;
import com.umudugudu.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

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

    void sendToUsers(List<User> users, String title, String message);
    boolean shouldSendNotification(User user, NotificationType type);
}
