package com.umudugudu.repository;

import com.umudugudu.entity.NotificationPreference;
import com.umudugudu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    Optional<NotificationPreference> findByUser(User user);
}
