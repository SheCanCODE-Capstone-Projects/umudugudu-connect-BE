package com.umudugudu.service.impl;

import com.umudugudu.dto.response.NotificationResponse;
import com.umudugudu.entity.Activity;
import com.umudugudu.entity.Notification;
import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.event.ActivityCreatedEvent;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.repository.ActivityRepository;
import com.umudugudu.repository.NotificationRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.FcmService;
import com.umudugudu.service.NotificationService;
import com.umudugudu.util.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEE dd MMM yyyy 'at' HH:mm");

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;
    private final ActivityRepository     activityRepository;
    private final FcmService             fcmService;
    private final SmsService             smsService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyActivityCreated(ActivityCreatedEvent event) {
        Long activityId = event.activityId();
        // Re-fetch the activity inside the async thread to avoid detached entity issues
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + activityId));

        List<User> citizens = userRepository.findAllByRole(Role.CITIZEN);
        if (citizens.isEmpty()) {
            log.info("No citizens to notify for activity id={}", activity.getId());
            return;
        }

        String title   = "New Activity: " + activity.getName();
        String dateStr = activity.getActivityDate().format(DATE_FMT);
        String body    = String.format("%s — %s at %s", activity.getName(), dateStr, activity.getLocation());
        String deepLink = "activity/" + activity.getId();

        for (User citizen : citizens) {
            // 1. Persist in-app notification
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setBody(body);
            notification.setDeepLink(deepLink);
            notification.setRecipient(citizen);
            notificationRepository.save(notification);

            // 2. Push notification (FCM) — if the citizen has a device token
            if (citizen.getFcmToken() != null && !citizen.getFcmToken().isBlank()) {
                fcmService.sendPush(citizen.getFcmToken(), title, body, deepLink);
            } else {
                // 3. SMS fallback — for citizens without the app / offline
                if (citizen.getPhoneNumber() != null && !citizen.getPhoneNumber().isBlank()) {
                    String sms = String.format(
                            "New activity: %s on %s at %s. Open UmuduguduConnect for details.",
                            activity.getName(), dateStr, activity.getLocation()
                    );
                    smsService.sendSms(citizen.getPhoneNumber(), sms);
                }
            }
        }

        log.info("Notified {} citizens about activity id={}", citizens.size(), activity.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .body(n.getBody())
                        .deepLink(n.getDeepLink())
                        .read(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build());
    }

    @Override
    @Transactional
    public void markRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new SecurityException("Not authorized to mark this notification as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
