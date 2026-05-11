package com.umudugudu.service.impl;

import com.umudugudu.dto.request.CreateActivityRequest;
import com.umudugudu.entity.Activity;
import com.umudugudu.entity.ActivityCreatedEvent;
import com.umudugudu.entity.ActivityStatus;
import com.umudugudu.repository.ActivityRepository;
import com.umudugudu.service.ActivityService;
import com.umudugudu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    @Override
    public Activity createActivity(CreateActivityRequest request, UUID leaderId) {
        Activity activity = Activity.builder()
                .villageId(request.getVillageId())
                .createdBy(leaderId)
                .title(request.getTitle())
                .scheduledAt(request.getScheduledAt())
                .location(request.getLocation())
                .type(request.getType())
                .status(ActivityStatus.SCHEDULED)
                .build();

        Activity saved = activityRepository.save(activity);
        eventPublisher.publishEvent(new ActivityCreatedEvent(saved));
        return saved;
    }
}
