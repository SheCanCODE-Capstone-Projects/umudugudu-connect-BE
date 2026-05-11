package com.umudugudu.repository;

import com.umudugudu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    List<Notification> findByRecipientIdAndReadFalse(Long recipientId);
    long countByRecipientIdAndReadFalse(Long recipientId);
}