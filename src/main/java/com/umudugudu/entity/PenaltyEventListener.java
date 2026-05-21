package com.umudugudu.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PenaltyEventListener {
    @EventListener
    public void handlePenaltyCreated(PenaltyCreatedEvent event) {

        String message = "You have been assigned a penalty of "
                + event.getAmount()
                + " RWF. Reason: "
                + event.getReason();

        log.info("SMS to {}: {}", event.getCitizen().getPhoneNumber(), message);

        // simulate SMS
        System.out.println(
                "SMS to " +
                        event.getCitizen().getPhoneNumber() +
                        ": " +
                        message
        );
    }
}
