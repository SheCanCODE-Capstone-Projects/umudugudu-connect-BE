package com.umudugudu.service.impl;

import com.umudugudu.dto.request.CreateAnnouncementRequest;
import com.umudugudu.dto.response.AnnouncementResponse;
import com.umudugudu.entity.Announcement;
import com.umudugudu.entity.AnnouncementStatus;
import com.umudugudu.entity.TargetType;
import com.umudugudu.entity.User;
import com.umudugudu.repository.AnnouncementRepository;
import com.umudugudu.service.AnnouncementService;
import com.umudugudu.service.NotificationService;
import com.umudugudu.service.RecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final RecipientService recipientService;
    private final NotificationService notificationService;

    @Override
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request) {

        AnnouncementStatus status;

        if (request.getScheduledAt() != null &&
                request.getScheduledAt().isAfter(LocalDateTime.now())) {

            status = AnnouncementStatus.SCHEDULED;

        } else {
            status = AnnouncementStatus.SENT;
        }

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .scheduledAt(request.getScheduledAt())
                .sentAt(status == AnnouncementStatus.SENT
                        ? LocalDateTime.now()
                        : null)
                .status(status)
                .targetType(
                        request.getIsibIds() == null ||
                                request.getIsibIds().isEmpty()
                                ? TargetType.ALL
                                : TargetType.ISIB
                )
                .build();

        Announcement saved =
                announcementRepository.save(announcement);



        List<User> recipients =
                recipientService.getRecipients(
                        request.getVillageId(),
                        request.getIsibIds()
                );

        if (status == AnnouncementStatus.SENT) {

            notificationService.sendToUsers(
                    recipients,
                    saved.getTitle(),
                    saved.getMessage()
            );
        }

        return AnnouncementResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .message(saved.getMessage())
                .status(saved.getStatus().name())
                .scheduledAt(saved.getScheduledAt())
                .sentAt(saved.getSentAt())
                .build();
    }
}