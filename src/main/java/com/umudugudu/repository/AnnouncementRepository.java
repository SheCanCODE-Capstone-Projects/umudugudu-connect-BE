package com.umudugudu.repository;

import com.umudugudu.entity.Announcement;
import com.umudugudu.entity.AnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    List<Announcement> findByStatusAndScheduledAtBefore(
            AnnouncementStatus status,
            LocalDateTime time
    );
}
