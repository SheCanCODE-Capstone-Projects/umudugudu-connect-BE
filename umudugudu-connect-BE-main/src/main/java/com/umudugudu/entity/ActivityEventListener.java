package com.umudugudu.entity;

import com.umudugudu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityEventListener {
    private final NotificationService notificationService;

    @EventListener
    public void handleActivityCreated(ActivityCreatedEvent event) {

        notificationService.notifyVillage(event.getActivity());

        log.info("Notifications sent successfully");
    }
}
