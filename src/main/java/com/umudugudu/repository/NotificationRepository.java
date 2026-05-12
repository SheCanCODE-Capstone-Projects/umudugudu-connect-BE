package com.umudugudu.repository;

import com.umudugudu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
    List<Notification> findByRecipientIdAndReadFalse(UUID recipientId);
    UUID countByRecipientIdAndReadFalse(UUID recipientId);
}