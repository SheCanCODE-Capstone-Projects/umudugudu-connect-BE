package com.umudugudu.service.impl;

import com.umudugudu.dto.request.BulkAttendanceSyncRequest;
import com.umudugudu.dto.request.MarkAttendanceRequest;
import com.umudugudu.dto.response.AttendanceResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.ActivityRepository;
import com.umudugudu.repository.AttendanceRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.AttendanceService;
import com.umudugudu.service.PenaltyFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final PenaltyFlagService penaltyFlagService;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(UUID activityId,
                                             MarkAttendanceRequest request,
                                             User isiboLeader) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));

        validateIsiboLeaderCanMark(isiboLeader, activity);

        User citizen = userRepository.findById(request.getCitizenId())
                .orElseThrow(() -> new RuntimeException("Citizen not found: " + request.getCitizenId()));

        Attendance attendance = attendanceRepository
                .findByActivityIdAndCitizenId(activityId, citizen.getId())
                .orElse(Attendance.builder()
                        .activityId(activityId)
                        .citizen(citizen)
                        .markedBy(isiboLeader)
                        .build());

        attendance.setStatus(request.getStatus());
        attendance.setMarkedBy(isiboLeader);
        attendance.setSyncedFromOffline(request.isSyncedFromOffline());
        attendance.setOfflineMarkedAt(request.getOfflineMarkedAt());

        Attendance saved = attendanceRepository.save(attendance);

        log.info("Attendance saved — activity: {}, citizen: {}, status: {}",
                activityId, citizen.getId(), request.getStatus());

        penaltyFlagService.handleAttendanceStatus(saved);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public List<AttendanceResponse> syncOfflineAttendance(BulkAttendanceSyncRequest request,
                                                          User isiboLeader) {

        log.info("Syncing {} offline records for activity {}",
                request.getRecords().size(), request.getActivityId());

        return request.getRecords().stream()
                .map(record -> {
                    record.setSyncedFromOffline(true);
                    return markAttendance(request.getActivityId(), record, isiboLeader);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceForActivity(UUID activityId) {

        return attendanceRepository.findByActivityId(activityId)
                .stream()
                .map(attendance -> toResponse(attendance))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAbsentMembers(UUID activityId) {

        return attendanceRepository.findByActivityIdAndStatus(activityId, AttendanceStatus.ABSENT)
                .stream()
                .map(attendance -> toResponse(attendance))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceForIsibo(UUID isiboId) {

        return attendanceRepository.findByCitizen_Isibo_Id(isiboId)
                .stream()
                .map(attendance -> toResponse((Attendance) attendance))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceForCitizen(UUID citizenId) {

        return attendanceRepository.findByCitizen_Id(citizenId)
                .stream()
                .map(attendance -> toResponse(attendance))
                .collect(Collectors.toList());
    }

    private void validateIsiboLeaderCanMark(User isiboLeader, Activity activity) {

        if (isiboLeader.getIsibo() == null)
            throw new RuntimeException("You are not assigned to any isibo.");

        if (isiboLeader.getIsibo().getVillage() == null ||
                !isiboLeader.getIsibo().getVillage().getId().toString()
                        .equals(activity.getVillageId().toString()))
            throw new RuntimeException("You are not authorised to mark attendance for this activity.");

        if (activity.getStatus() == ActivityStatus.CANCELLED)
            throw new RuntimeException("Cannot mark attendance for a cancelled activity.");

        if (activity.getStatus() == ActivityStatus.SCHEDULED)
            throw new RuntimeException("Activity has not started yet.");
    }

    private AttendanceResponse toResponse(Attendance a) {

        AttendanceResponse r = new AttendanceResponse();

        r.setId(a.getId());
        r.setActivityId(a.getActivityId());
        r.setCitizenId(a.getCitizen().getId());
        r.setCitizenFullName(a.getCitizen().getFirstName() + " " + a.getCitizen().getLastName());
        r.setMarkedByFullName(a.getMarkedBy().getFirstName() + " " + a.getMarkedBy().getLastName());
        r.setStatus(a.getStatus());
        r.setMarkedAt(a.getMarkedAt());
        r.setSyncedFromOffline(a.isSyncedFromOffline());
        r.setOfflineMarkedAt(a.getOfflineMarkedAt());

        return r;
    }
}