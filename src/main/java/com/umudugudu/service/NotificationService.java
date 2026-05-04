package com.umudugudu.service;


import com.umudugudu.dto.request.NotificationRequest;
import com.umudugudu.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    /**
     * Send notification with automatic fallback to SMS if internet unavailable
     */
    void notifyWithFallback(UUID userId, String title, String message);

    /**
     * Broadcast notification to multiple users
     */
    void broadcastWithFallback(NotificationRequest request);

    /**
     * Get paginated notifications for current user
     */
    Page<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable);

    /**
     * Mark notification as read
     */
    void markAsRead(UUID notificationId, UUID userId);

    /**
     * Get unread notification count
     */
    long getUnreadCount(UUID userId);
}

