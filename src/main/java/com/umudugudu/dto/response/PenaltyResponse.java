package com.umudugudu.dto.response;

import lombok.Data;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

public record PenaltyResponse(UUID id,
                              BigDecimal amount,
                              String reason,
                              String status) {

}
