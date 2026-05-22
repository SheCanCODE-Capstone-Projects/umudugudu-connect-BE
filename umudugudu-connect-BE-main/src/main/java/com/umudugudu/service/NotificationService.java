package com.umudugudu.service;

import com.umudugudu.entity.Activity;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService  {
    void notifyVillage(Activity activity);

    void sendPushNotification(Activity activity);

    void sendSmsNotification(Activity activity);
}
