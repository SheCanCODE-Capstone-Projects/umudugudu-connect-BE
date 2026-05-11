package com.umudugudu.service;

import com.umudugudu.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /** Return paginated notifications for the given user. */
    Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable);

    /** Mark a single notification as read. */
    void markRead(Long notificationId, Long userId);
}
