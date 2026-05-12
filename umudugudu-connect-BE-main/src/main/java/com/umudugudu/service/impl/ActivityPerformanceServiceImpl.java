package com.umudugudu.service.impl;

import com.umudugudu.dto.response.ActivityPerformanceResponse;
import com.umudugudu.dto.response.HouseholdAttendanceResponse;
import com.umudugudu.dto.response.IsiboPerformanceResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.ActivityRepository;
import com.umudugudu.repository.AttendanceRepository;
import com.umudugudu.repository.IsiboRepository;
import com.umudugudu.service.ActivityPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityPerformanceServiceImpl implements ActivityPerformanceService {

    private final ActivityRepository   activityRepository;
    private final AttendanceRepository attendanceRepository;
    private final IsiboRepository      isiboRepository;



    @Override
    public ActivityPerformanceResponse getActivityPerformance(UUID activityId, User villageLeader) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));


        assertVillageLeaderOwnsActivity(villageLeader, activity);

        List<Isibo> isibs = getIsibosByVillageLeader(villageLeader);
        List<Attendance> records = attendanceRepository.findByActivityId(activityId);

        return buildPerformance(activity, isibs, records);
    }

    @Override
    public List<ActivityPerformanceResponse> getAllActivityPerformance(User villageLeader) {

        UUID leaderUUID = UUID.fromString(villageLeader.getId().toString());


        List<Activity> activities = activityRepository.findByCreatedBy(leaderUUID);

        // Isibs beUUID to the village via Village entity (UUID id) — safe to use
        Village village = resolveVillage(villageLeader);
        List<Isibo> isibs = isiboRepository.findByVillageId(village.getId());

        return activities.stream()
                .map(activity -> {
                    List<Attendance> records = attendanceRepository.findByActivityId(activity.getId());
                    return buildPerformance(activity, isibs, records);
                })
                .collect(Collectors.toList());
    }



    private ActivityPerformanceResponse buildPerformance(Activity activity,
                                                         List<Isibo> isibs,
                                                         List<Attendance> records) {
        Map<UUID, Attendance> attendanceMap = records.stream()
                .collect(Collectors.toMap(
                        a -> a.getCitizen().getId(),
                        a -> a,
                        (a1, a2) -> a1
                ));

        List<IsiboPerformanceResponse> isiboBreakdown = isibs.stream()
                .map(isibo -> buildIsiboPerformance(isibo, attendanceMap))
                .collect(Collectors.toList());

        int totalInvited = isiboBreakdown.stream().mapToInt(IsiboPerformanceResponse::getTotalInvited).sum();
        int totalPresent = isiboBreakdown.stream().mapToInt(IsiboPerformanceResponse::getTotalPresent).sum();
        int totalAbsent  = totalInvited - totalPresent;
        double pct       = totalInvited == 0 ? 0.0 : Math.round((totalPresent * 100.0 / totalInvited) * 10) / 10.0;

        return ActivityPerformanceResponse.builder()
                .activityId(activity.getId())
                .title(activity.getTitle())
                .type(activity.getType().name())
                .status(activity.getStatus().name())
                .scheduledAt(activity.getScheduledAt())
                .location(activity.getLocation())
                .totalInvited(totalInvited)
                .totalPresent(totalPresent)
                .totalAbsent(totalAbsent)
                .participationPercentage(pct)
                .isiboBreakdown(isiboBreakdown)
                .build();
    }

    private IsiboPerformanceResponse buildIsiboPerformance(Isibo isibo,
                                                           Map<UUID, Attendance> attendanceMap) {
        List<User> citizens = isibo.getCitizens();

        List<HouseholdAttendanceResponse> households = citizens.stream()
                .map(citizen -> {
                    Attendance att = attendanceMap.get(citizen.getId());
                    return HouseholdAttendanceResponse.builder()
                            .citizenId(citizen.getId())
                            .citizenFullName(citizen.getFirstName() + " " + citizen.getLastName())
                            .status(att != null ? att.getStatus() : null)
                            .markedAt(att != null ? att.getMarkedAt() : null)
                            .syncedFromOffline(att != null && att.isSyncedFromOffline())
                            .build();
                })
                .collect(Collectors.toList());

        int invited = citizens.size();
        int present = (int) households.stream()
                .filter(h -> AttendanceStatus.PRESENT.equals(h.getStatus()))
                .count();
        int absent  = invited - present;
        double pct  = invited == 0 ? 0.0 : Math.round((present * 100.0 / invited) * 10) / 10.0;

        return IsiboPerformanceResponse.builder()
                .isiboId(isibo.getId())
                .isiboName(isibo.getName())
                .totalInvited(invited)
                .totalPresent(present)
                .totalAbsent(absent)
                .participationPercentage(pct)
                .householdAttendance(households)
                .build();
    }


    private Village resolveVillage(User villageLeader) {
        Village village = villageLeader.getVillage();
        if (village == null) {
            throw new RuntimeException("No village assigned to leader id: " + villageLeader.getId());
        }
        return village;
    }

    private List<Isibo> getIsibosByVillageLeader(User villageLeader) {
        return isiboRepository.findByVillageId(resolveVillage(villageLeader).getId());
    }

    private void assertVillageLeaderOwnsActivity(User villageLeader, Activity activity) {
        UUID leaderUUID = UUID.fromString(villageLeader.getId().toString());
        if (!activity.getCreatedBy().equals(leaderUUID)) {
            throw new RuntimeException("You are not authorised to view this activity's performance.");
        }
    }
}