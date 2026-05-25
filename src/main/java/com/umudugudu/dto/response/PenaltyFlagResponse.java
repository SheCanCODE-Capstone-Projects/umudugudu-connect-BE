package com.umudugudu.dto.response;

<<<<<<< HEAD
import com.umudugudu.entity.PenaltyStatus;
import lombok.Data;
=======
import com.umudugudu.entity.PenaltyPaymentStatus;
import com.umudugudu.entity.PenaltyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)

import java.time.LocalDateTime;
import java.util.UUID;

<<<<<<< HEAD
@Data
=======

@Data
@NoArgsConstructor
@AllArgsConstructor

>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
public class PenaltyFlagResponse {

    private UUID id;
    private UUID activityId;
    private UUID citizenId;
    private String citizenFullName;
    private UUID attendanceId;
    private PenaltyStatus status;
<<<<<<< HEAD
=======
    private PenaltyPaymentStatus paymentStatus;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    private String reviewNote;
    private String reviewedByFullName;
    private LocalDateTime flaggedAt;
    private LocalDateTime reviewedAt;
<<<<<<< HEAD
=======


>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
}