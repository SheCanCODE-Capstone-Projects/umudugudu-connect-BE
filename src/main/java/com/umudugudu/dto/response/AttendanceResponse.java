package com.umudugudu.dto.response;

import com.umudugudu.entity.AttendanceStatus;
import lombok.Data;
<<<<<<< HEAD
=======
import lombok.Getter;
import lombok.Setter;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)

import java.time.LocalDateTime;
import java.util.UUID;

@Data
<<<<<<< HEAD
=======
@Setter
@Getter
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
public class AttendanceResponse {

    private UUID id;
    private UUID activityId;
    private UUID citizenId;
    private String citizenFullName;
    private String markedByFullName;
    private AttendanceStatus status;
    private LocalDateTime markedAt;
    private boolean syncedFromOffline;
    private LocalDateTime offlineMarkedAt;
<<<<<<< HEAD
=======

    public void setCitizenId(UUID id) {
    }
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
}