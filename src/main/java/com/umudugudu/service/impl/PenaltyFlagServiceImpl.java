package com.umudugudu.service.impl;

import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.HouseholdPenaltySummary;
import com.umudugudu.dto.response.IsiboHouseholdPenaltyOverview;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.PenaltyFlagRepository;
import com.umudugudu.repository.IsiboRepository;
import com.umudugudu.service.PenaltyFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyFlagServiceImpl implements PenaltyFlagService {

    private final PenaltyFlagRepository penaltyFlagRepository;
    private final IsiboRepository isiboRepository;

    @Override
    @Transactional
    public void handleAttendanceStatus(Attendance attendance) {
<<<<<<< HEAD
        UUID activityId = attendance.getActivityId();
        UUID citizenId  = attendance.getCitizen().getId();

        if (attendance.getStatus() == AttendanceStatus.ABSENT) {

            boolean alreadyFlagged = penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .isPresent();

            if (!alreadyFlagged) {
=======

        UUID activityId = attendance.getActivityId();
        UUID citizenId = attendance.getCitizen().getId();

        if (attendance.getStatus() == AttendanceStatus.ABSENT) {

            boolean exists = penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .isPresent();

            if (!exists) {
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
                PenaltyFlag flag = PenaltyFlag.builder()
                        .activityId(activityId)
                        .citizen(attendance.getCitizen())
                        .attendance(attendance)
<<<<<<< HEAD
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
=======
                        .reviewStatus(PenaltyStatus.FLAGGED)
                        .paymentStatus(PenaltyPaymentStatus.UNPAID)
                        .flaggedAt(LocalDateTime.now())
                        .build();

                penaltyFlagRepository.save(flag);

                log.info("Penalty flag created for citizen {} on activity {}", citizenId, activityId);
            }
        }

        else if (attendance.getStatus() == AttendanceStatus.PRESENT) {

            penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .filter(f -> f.getReviewStatus() == PenaltyStatus.FLAGGED)
                    .ifPresent(flag -> {
                        penaltyFlagRepository.delete(flag);
                        log.info("Penalty flag removed for citizen {}", citizenId);
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
                    });
        }
    }

<<<<<<< HEAD

=======
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    @Override
    @Transactional
    public PenaltyFlagResponse reviewPenalty(UUID flagId,
                                             ReviewPenaltyRequest request,
                                             User villageLeader) {

        PenaltyFlag flag = penaltyFlagRepository.findById(flagId)
<<<<<<< HEAD
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
=======
                .orElseThrow(() -> new RuntimeException("Penalty flag not found"));

        if (flag.getReviewStatus() == PenaltyStatus.CONFIRMED ||
                flag.getReviewStatus() == PenaltyStatus.WAIVED) {
            throw new RuntimeException("Already reviewed");
        }

        flag.setReviewStatus(request.getDecision());
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
        flag.setReviewNote(request.getReviewNote());
        flag.setReviewedBy(villageLeader);
        flag.setReviewedAt(LocalDateTime.now());

<<<<<<< HEAD

=======
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
        return toResponse(penaltyFlagRepository.save(flag));
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository.findByActivityId(activityId)
<<<<<<< HEAD
                .stream().map(this::toResponse).collect(Collectors.toList());
=======
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    }

    @Override
    public List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId) {
<<<<<<< HEAD
        return penaltyFlagRepository.findByActivityIdAndStatus(activityId, PenaltyStatus.FLAGGED)
                .stream().map(this::toResponse).collect(Collectors.toList());
=======
        return penaltyFlagRepository
                .findByActivityIdAndReviewStatus(activityId, PenaltyStatus.FLAGGED)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForCitizen(UUID citizenId) {
        return penaltyFlagRepository.findByCitizenId(citizenId)
<<<<<<< HEAD
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
=======
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PenaltyFlagResponse toResponse(PenaltyFlag f) {

        PenaltyFlagResponse r = new PenaltyFlagResponse();

        r.setId(f.getId());
        r.setActivityId(f.getActivityId());
        r.setCitizenId(f.getCitizen().getId());
        r.setCitizenFullName(
                f.getCitizen().getFirstName() + " " + f.getCitizen().getLastName()
        );
        r.setAttendanceId(f.getAttendance().getId());

        r.setStatus(f.getReviewStatus());
        r.setPaymentStatus(f.getPaymentStatus());

        r.setReviewNote(f.getReviewNote());
        r.setFlaggedAt(f.getFlaggedAt());
        r.setReviewedAt(f.getReviewedAt());

>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
        if (f.getReviewedBy() != null) {
            r.setReviewedByFullName(
                    f.getReviewedBy().getFirstName() + " " + f.getReviewedBy().getLastName()
            );
        }
<<<<<<< HEAD
        r.setFlaggedAt(f.getFlaggedAt());
        r.setReviewedAt(f.getReviewedAt());
=======

>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
        return r;
    }

    @Override
    @Transactional(readOnly = true)
    public IsiboHouseholdPenaltyOverview getHouseholdPenaltiesByIsibo(UUID isiboId) {

        Isibo isibo = isiboRepository.findById(isiboId)
                .orElseThrow(() -> new RuntimeException("Isibo not found: " + isiboId));


        List<PenaltyFlag> allPenalties = penaltyFlagRepository.findByCitizen_IsiboId(isiboId);


        Map<User, List<PenaltyFlag>> householdPenalties = allPenalties.stream()
                .collect(Collectors.groupingBy(PenaltyFlag::getCitizen));


        List<HouseholdPenaltySummary> householdSummaries = householdPenalties.entrySet().stream()
                .map(entry -> {
                    User householdHead = entry.getKey();
                    List<PenaltyFlag> penalties = entry.getValue();


                    long unpaidCount = penalties.stream()
                            .filter(p -> p.getStatus() == PenaltyStatus.FLAGGED)
                            .count();


                    BigDecimal totalAmount = BigDecimal.valueOf(unpaidCount * 1000);

                    return HouseholdPenaltySummary.builder()
                            .householdId(householdHead.getId())
                            .householdHeadName(householdHead.getFirstName() + " " + householdHead.getLastName())
                            .phoneNumber(householdHead.getPhoneNumber())
                            .totalUnpaidPenalties(unpaidCount)
                            .totalOutstandingAmount(totalAmount)
                            .penaltyDetails(penalties.stream()
                                    .filter(p -> p.getStatus() == PenaltyStatus.FLAGGED)
                                    .map(this::toResponse)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .filter(h -> h.getTotalUnpaidPenalties() > 0) // Only include households with pending penalties
                .collect(Collectors.toList());


        long totalUnpaidPenalties = householdSummaries.stream()
                .mapToLong(HouseholdPenaltySummary::getTotalUnpaidPenalties)
                .sum();

        BigDecimal totalOutstandingAmount = householdSummaries.stream()
                .map(HouseholdPenaltySummary::getTotalOutstandingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return IsiboHouseholdPenaltyOverview.builder()
                .isiboId(isibo.getId())
                .isiboName(isibo.getName())
                .totalHouseholdsWithPenalties((long) householdSummaries.size())
                .totalUnpaidPenalties(totalUnpaidPenalties)
                .totalOutstandingAmount(totalOutstandingAmount)
                .households(householdSummaries)
                .build();
    }
}