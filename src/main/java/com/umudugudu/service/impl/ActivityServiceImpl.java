package com.umudugudu.service.impl;

import com.umudugudu.dto.request.ActivityRequest;
import com.umudugudu.dto.response.ActivityResponse;
import com.umudugudu.entity.Activity;
import com.umudugudu.entity.User;
import com.umudugudu.event.ActivityCreatedEvent;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.repository.ActivityRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository       activityRepository;
    private final UserRepository           userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ActivityResponse create(ActivityRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Activity activity = new Activity();
        activity.setName(request.getName());
        activity.setDescription(request.getDescription());
        activity.setActivityDate(request.getActivityDate());
        activity.setLocation(request.getLocation());
        activity.setCreatedBy(creator);

        Activity saved = activityRepository.save(activity);

        // Publish event — notification fires AFTER this transaction commits
        eventPublisher.publishEvent(new ActivityCreatedEvent(saved.getId()));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityResponse> list(Pageable pageable) {
        return activityRepository.findAllByOrderByActivityDateAsc(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse getById(Long id) {
        return activityRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + id));
    }

    @Override
    @Transactional
    public ActivityResponse update(Long id, ActivityRequest request, Long requesterId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + id));

        activity.setName(request.getName());
        activity.setDescription(request.getDescription());
        activity.setActivityDate(request.getActivityDate());
        activity.setLocation(request.getLocation());

        return toResponse(activityRepository.save(activity));
    }

    private ActivityResponse toResponse(Activity a) {
        return ActivityResponse.builder()
                .id(a.getId())
                .name(a.getName())
                .description(a.getDescription())
                .activityDate(a.getActivityDate())
                .location(a.getLocation())
                .createdByName(a.getCreatedBy().getFirstName() + " " + a.getCreatedBy().getLastName())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
