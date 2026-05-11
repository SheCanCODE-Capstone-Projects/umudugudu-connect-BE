package com.umudugudu.service.impl;

import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.PenaltyFlagRepository;
import com.umudugudu.service.PenaltyFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyFlagServiceImpl implements PenaltyFlagService {

    private final PenaltyFlagRepository penaltyFlagRepository;

    @Override
    @Transactional
    public void handleAttendanceStatus(Attendance attendance) {
        UUID activityId = attendance.getActivityId();
        Long citizenId  = attendance.getCitizen().getId();

        if (attendance.getStatus() == AttendanceStatus.ABSENT) {

            boolean alreadyFlagged = penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .isPresent();

            if (!alreadyFlagged) {
                PenaltyFlag flag = PenaltyFlag.builder()
                        .activityId(activityId)
                        .citizen(attendance.getCitizen())
                        .attendance(attendance)
                        .build();

                penaltyFlagRepository.save(flag);
                log.info("Penalty flag created for citizen {} on activity {}", citizenId, activityId);
            }

        } else if (attendance.getStatus() == AttendanceStatus.PRESENT) {

            penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .filter(f -> f.getStatus() == PenaltyStatus.FLAGGED)
                    .ifPresent(flag -> {
                        penaltyFlagRepository.delete(flag);
                        log.info("Penalty flag removed for citizen {} — marked PRESENT", citizenId);
                    });
        }
    }


    @Override
    @Transactional
    public PenaltyFlagResponse reviewPenalty(Long flagId,
                                             ReviewPenaltyRequest request,
                                             User villageLeader) {

        PenaltyFlag flag = penaltyFlagRepository.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Penalty flag not found: " + flagId));

        if (flag.getStatus() == PenaltyStatus.CONFIRMED || flag.getStatus() == PenaltyStatus.WAIVED) {
            throw new RuntimeException("This penalty has already been reviewed.");
        }

        if (request.getDecision() != PenaltyStatus.CONFIRMED
                && request.getDecision() != PenaltyStatus.WAIVED) {
            throw new RuntimeException("Decision must be CONFIRMED or WAIVED.");
        }

        if (request.getDecision() == PenaltyStatus.WAIVED
                && (request.getReviewNote() == null || request.getReviewNote().isBlank())) {
            throw new RuntimeException("A review note is required when waiving a penalty.");
        }

        flag.setStatus(request.getDecision());
        flag.setReviewNote(request.getReviewNote());
        flag.setReviewedBy(villageLeader);
        flag.setReviewedAt(LocalDateTime.now());


        return toResponse(penaltyFlagRepository.save(flag));
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository.findByActivityId(activityId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository.findByActivityIdAndStatus(activityId, PenaltyStatus.FLAGGED)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForCitizen(Long citizenId) {
        return penaltyFlagRepository.findByCitizenId(citizenId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private PenaltyFlagResponse toResponse(PenaltyFlag f) {
        PenaltyFlagResponse r = new PenaltyFlagResponse();
        r.setId(f.getId());
        r.setActivityId(f.getActivityId());
        r.setCitizenId(f.getCitizen().getId());
        r.setCitizenFullName(f.getCitizen().getFirstName() + " " + f.getCitizen().getLastName());
        r.setAttendanceId(f.getAttendance().getId());
        r.setStatus(f.getStatus());
        r.setReviewNote(f.getReviewNote());
        if (f.getReviewedBy() != null) {
            r.setReviewedByFullName(
                    f.getReviewedBy().getFirstName() + " " + f.getReviewedBy().getLastName()
            );
        }
        r.setFlaggedAt(f.getFlaggedAt());
        r.setReviewedAt(f.getReviewedAt());
        return r;
    }
}