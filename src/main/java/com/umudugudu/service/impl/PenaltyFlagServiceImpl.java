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

        UUID activityId = attendance.getActivityId();
        UUID citizenId = attendance.getCitizen().getId();

        if (attendance.getStatus() == AttendanceStatus.ABSENT) {

            boolean exists = penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .isPresent();

            if (!exists) {
                PenaltyFlag flag = PenaltyFlag.builder()
                        .activityId(activityId)
                        .citizen(attendance.getCitizen())
                        .attendance(attendance)
                        .reviewStatus(PenaltyStatus.FLAGGED)
                        .paymentStatus(PenaltyPaymentStatus.UNPAID)
                        .flaggedAt(LocalDateTime.now())
                        .build();

                penaltyFlagRepository.save(flag);
                log.info("Penalty flag created for citizen {} on activity {}", citizenId, activityId);
            }

        } else if (attendance.getStatus() == AttendanceStatus.PRESENT) {

            penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .filter(f -> f.getReviewStatus() == PenaltyStatus.FLAGGED)
                    .ifPresent(flag -> {
                        penaltyFlagRepository.delete(flag);
                        log.info("Penalty flag removed for citizen {}", citizenId);
                    });
        }
    }

    @Override
    @Transactional
    public PenaltyFlagResponse reviewPenalty(UUID flagId,
                                             ReviewPenaltyRequest request,
                                             User villageLeader) {

        PenaltyFlag flag = penaltyFlagRepository.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Penalty flag not found"));

        if (flag.getReviewStatus() == PenaltyStatus.CONFIRMED ||
                flag.getReviewStatus() == PenaltyStatus.WAIVED) {
            throw new RuntimeException("Already reviewed");
        }

        flag.setReviewStatus(request.getDecision());
        flag.setReviewNote(request.getReviewNote());
        flag.setReviewedBy(villageLeader);
        flag.setReviewedAt(LocalDateTime.now());

        return toResponse(penaltyFlagRepository.save(flag));
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository.findByActivityId(activityId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository
                .findByActivityIdAndReviewStatus(activityId, PenaltyStatus.FLAGGED)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForCitizen(UUID citizenId) {
        return penaltyFlagRepository.findByCitizenId(citizenId)
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

        if (f.getReviewedBy() != null) {
            r.setReviewedByFullName(
                    f.getReviewedBy().getFirstName() + " " + f.getReviewedBy().getLastName()
            );
        }

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
                            .filter(p -> p.getReviewStatus() == PenaltyStatus.FLAGGED)
                            .count();

                    BigDecimal totalAmount = BigDecimal.valueOf(unpaidCount * 1000);

                    return HouseholdPenaltySummary.builder()
                            .householdId(householdHead.getId())
                            .householdHeadName(householdHead.getFirstName() + " " + householdHead.getLastName())
                            .phoneNumber(householdHead.getPhoneNumber())
                            .totalUnpaidPenalties(unpaidCount)
                            .totalOutstandingAmount(totalAmount)
                            .penaltyDetails(penalties.stream()
                                    .filter(p -> p.getReviewStatus() == PenaltyStatus.FLAGGED)
                                    .map(this::toResponse)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .filter(h -> h.getTotalUnpaidPenalties() > 0)
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