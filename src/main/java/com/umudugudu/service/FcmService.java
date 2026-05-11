package com.umudugudu.service;

/**
 * Sends Firebase Cloud Messaging push notifications to individual device tokens.
 */
public interface FcmService {

    /**
     * Send a push notification to a single FCM device token.
     *
     * @param fcmToken  the recipient's device token
     * @param title     notification title
     * @param body      notification body
     * @param deepLink  data payload for deep-linking (e.g. "activity/42")
     */
    void sendPush(String fcmToken, String title, String body, String deepLink);
}
