package com.umudugudu.entity;

import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class PenaltyCreatedEvent {
    private final User citizen;
    private final BigDecimal amount;
    private final String reason;

    public PenaltyCreatedEvent(User citizen, BigDecimal amount, String reason) {
        this.citizen = citizen;
        this.amount = amount;
        this.reason = reason;
    }
}
