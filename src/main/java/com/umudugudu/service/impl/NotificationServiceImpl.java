package com.umudugudu.service.impl;

import com.umudugudu.dto.request.NotificationRequest;
import com.umudugudu.dto.response.NotificationResponse;
import com.umudugudu.entity.Notification;
import com.umudugudu.entity.User;
import com.umudugudu.repository.NotificationRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.NotificationService;
import com.umudugudu.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    @Override
    @Async
    public void notifyWithFallback(UUID userId, String title, String message) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", userId);
                return;
            }

            User user = userOpt.get();


            boolean fcmSuccess = tryFcmNotification(user, title, message);

            if (fcmSuccess) {

                saveNotification(userId, title, message, Notification.DeliveryMethod.FCM);
            } else {

                log.warn("FCM failed for user {}, falling back to SMS", userId);
                boolean smsSuccess = smsService.notifyActivityViaSms(userId,
                        title + ": " + message);

                if (smsSuccess) {
                    saveNotification(userId, title, message, Notification.DeliveryMethod.SMS);
                } else {
                    log.error("Both FCM and SMS failed for user {}", userId);
                    saveNotification(userId, title, message, Notification.DeliveryMethod.EMAIL);
                }
            }
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }

    @Override
    public void broadcastWithFallback(NotificationRequest request) {
        try {
            List<User> recipients;

            if (request.getTargetUserId() != null) {

                Optional<User> userOpt = userRepository.findById(request.getTargetUserId());
                recipients = userOpt.map(List::of).orElse(List.of());
            } else if (request.getTargetIsibo() != null) {

                recipients = userRepository.findByIsiboId(request.getTargetIsibo());
            } else {

                recipients = userRepository.findAll();
            }

            for (User user : recipients) {
                notifyWithFallback(user.getId(), request.getTitle(), request.getMessage());
            }

            log.info("Notification broadcast to {} users", recipients.size());
        } catch (Exception e) {
            log.error("Error broadcasting notification: {}", e.getMessage());
        }
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return notifications.map(NotificationResponse::fromEntity);
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);

        if (notifOpt.isPresent()) {
            Notification notification = notifOpt.get();


            if (!notification.getUserId().equals(userId)) {
                log.warn("User {} attempted to mark notification {} as read", userId, notificationId);
                return;
            }

            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Override
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Private helper methods
     */

    private boolean tryFcmNotification(User user, String title, String message) {
        try {
            // TODO: Implement FCM integration

            log.debug("Sending FCM notification to user {}", user.getId());

            return false;
        } catch (Exception e) {
            log.error("FCM notification failed: {}", e.getMessage());
            return false;
        }
    }

    private void saveNotification(UUID userId, String title, String message,
                                  Notification.DeliveryMethod method) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setDeliveryMethod(method);
        notification.setIsRead(false);

        notificationRepository.save(notification);
        log.debug("Notification saved for user {} via {}", userId, method);
    }
}
