package com.umudugudu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


public record AssignPenaltyRequest(   @NotNull
                                      UUID attendanceId,

                                      @NotNull
                                      BigDecimal amount,

                                      @NotNull
                                      String reason,

                                      Boolean exemption
) {

}
