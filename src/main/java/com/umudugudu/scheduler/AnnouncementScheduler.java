package com.umudugudu.scheduler;

import com.umudugudu.entity.Announcement;
import com.umudugudu.entity.AnnouncementStatus;
import com.umudugudu.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
@Component
@RequiredArgsConstructor
public class AnnouncementScheduler {
    private final AnnouncementRepository announcementRepository;

    @Scheduled(fixedRate = 60000)
    public void sendScheduledAnnouncements() {

        List<Announcement> announcements =
                announcementRepository
                        .findByStatusAndScheduledAtBefore(
                                AnnouncementStatus.SCHEDULED,
                                LocalDateTime.now()
                        );

        for (Announcement announcement : announcements) {


            announcement.setStatus(AnnouncementStatus.SENT);
            announcement.setSentAt(LocalDateTime.now());

            announcementRepository.save(announcement);
        }
    }
}
