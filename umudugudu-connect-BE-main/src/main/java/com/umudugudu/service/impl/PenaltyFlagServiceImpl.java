package com.umudugudu.service.impl;

import com.umudugudu.dto.request.AssignPenaltyRequest;
import com.umudugudu.dto.request.ExemptPenaltyRequest;
import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.IsiboPenaltySummaryResponse;
import com.umudugudu.dto.response.IsiboPenaltySummaryResponse.HouseholdPenaltySummary;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.dto.response.PenaltyResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.ActivityRepository;
import com.umudugudu.repository.NotificationRepository;
import com.umudugudu.repository.PenaltyFlagRepository;
import com.umudugudu.service.PenaltyFlagService;
import com.umudugudu.util.SmsService;
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
    private final NotificationRepository notificationRepository;
    private final ActivityRepository     activityRepository;
    private final SmsService             smsService;


    @Override
    @Transactional
    public void handleAttendanceStatus(Attendance attendance) {
        UUID activityId = attendance.getActivityId();
        UUID citizenId  = attendance.getCitizen().getId();

        if (attendance.getStatus() == AttendanceStatus.ABSENT) {
            boolean alreadyFlagged = penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId).isPresent();
            if (!alreadyFlagged) {
                penaltyFlagRepository.save(PenaltyFlag.builder()
                        .activityId(activityId)
                        .citizen(attendance.getCitizen())
                        .attendance(attendance)
                        .build());
                log.info("Penalty flag created — citizen {} activity {}", citizenId, activityId);
            }
        } else if (attendance.getStatus() == AttendanceStatus.PRESENT) {
            penaltyFlagRepository
                    .findByActivityIdAndCitizenId(activityId, citizenId)
                    .filter(f -> f.getStatus() == PenaltyStatus.FLAGGED)
                    .ifPresent(flag -> {
                        penaltyFlagRepository.delete(flag);
                        log.info("Penalty flag removed — citizen {} marked PRESENT", citizenId);
                    });
        }
    }

    @Override
    @Transactional
    public PenaltyFlagResponse reviewPenalty(UUID flagId, ReviewPenaltyRequest request, User villageLeader) {
        PenaltyFlag flag = findFlagOrThrow(flagId);

        if (flag.getStatus() == PenaltyStatus.CONFIRMED || flag.getStatus() == PenaltyStatus.WAIVED)
            throw new RuntimeException("This penalty has already been reviewed.");
        if (request.getDecision() != PenaltyStatus.CONFIRMED && request.getDecision() != PenaltyStatus.WAIVED)
            throw new RuntimeException("Decision must be CONFIRMED or WAIVED.");
        if (request.getDecision() == PenaltyStatus.WAIVED &&
                (request.getReviewNote() == null || request.getReviewNote().isBlank()))
            throw new RuntimeException("A review note is required when waiving a penalty.");

        flag.setStatus(request.getDecision());
        flag.setReviewNote(request.getReviewNote());
        flag.setReviewedBy(villageLeader);
        flag.setReviewedAt(LocalDateTime.now());

        return toLegacyResponse(penaltyFlagRepository.save(flag));
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository.findByActivityId(activityId)
                .stream().map(this::toLegacyResponse).collect(Collectors.toList());
    }

    @Override
    public List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId) {
        return penaltyFlagRepository.findByActivityIdAndStatus(activityId, PenaltyStatus.FLAGGED)
                .stream().map(this::toLegacyResponse).collect(Collectors.toList());
    }

    @Override
    public List<PenaltyFlagResponse> getFlagsForCitizen(UUID citizenId) {
        return penaltyFlagRepository.findByCitizenId(citizenId)
                .stream().map(this::toLegacyResponse).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public PenaltyResponse assignPenalty(UUID flagId, AssignPenaltyRequest request, User villageLeader) {
        PenaltyFlag flag = findFlagOrThrow(flagId);

        if (flag.getStatus() == PenaltyStatus.EXCUSED)
            throw new RuntimeException("Cannot assign penalty — citizen has been excused.");
        if (flag.getStatus() == PenaltyStatus.UNPAID || flag.getStatus() == PenaltyStatus.PAID)
            throw new RuntimeException("Penalty has already been assigned.");

        flag.setStatus(PenaltyStatus.UNPAID);
        flag.setAmount(request.getAmount());
        flag.setReason(request.getReason());
        flag.setDueDate(request.getDueDate());
        flag.setReviewedBy(villageLeader);
        flag.setReviewedAt(LocalDateTime.now());

        PenaltyFlag saved = penaltyFlagRepository.save(flag);
        log.info("Penalty assigned — citizen {} amount {} activity {}",
                flag.getCitizen().getId(), request.getAmount(), flag.getActivityId());

        // Notify the citizen
        String activityTitle = resolveActivityTitle(flag.getActivityId());
        String message = String.format(
                "A penalty of RWF %s has been assigned for your absence from '%s'. Reason: %s. Due: %s.",
                request.getAmount().toPlainString(), activityTitle,
                request.getReason(), request.getDueDate());

        sendNotification(flag.getCitizen(), message);
        sendSms(flag.getCitizen(), message);

        return toResponse(saved, activityTitle);
    }


    @Override
    @Transactional
    public PenaltyResponse exemptCitizen(UUID flagId, ExemptPenaltyRequest request, User villageLeader) {
        PenaltyFlag flag = findFlagOrThrow(flagId);

        if (flag.getStatus() == PenaltyStatus.UNPAID || flag.getStatus() == PenaltyStatus.PAID)
            throw new RuntimeException("Cannot exempt — penalty has already been assigned.");
        if (flag.getStatus() == PenaltyStatus.EXCUSED)
            throw new RuntimeException("Citizen is already marked as excused.");

        flag.setStatus(PenaltyStatus.EXCUSED);
        flag.setExemptionReason(request.getExemptionReason());
        flag.setReviewedBy(villageLeader);
        flag.setReviewedAt(LocalDateTime.now());

        PenaltyFlag saved = penaltyFlagRepository.save(flag);
        log.info("Citizen {} excused from penalty for activity {}",
                flag.getCitizen().getId(), flag.getActivityId());

        String activityTitle = resolveActivityTitle(flag.getActivityId());
        String message = String.format(
                "Your absence from '%s' has been marked as EXCUSED. Reason: %s.",
                activityTitle, request.getExemptionReason());

        sendNotification(flag.getCitizen(), message);
        sendSms(flag.getCitizen(), message);

        return toResponse(saved, activityTitle);
    }


    @Override
    public List<PenaltyResponse> getMyPenalties(User citizen) {
        return penaltyFlagRepository.findByCitizenId(citizen.getId())
                .stream()
                .filter(f -> f.getStatus() == PenaltyStatus.UNPAID
                        || f.getStatus() == PenaltyStatus.PAID
                        || f.getStatus() == PenaltyStatus.EXCUSED)
                .map(f -> toResponse(f, resolveActivityTitle(f.getActivityId())))
                .collect(Collectors.toList());
    }


    @Override
    public IsiboPenaltySummaryResponse getIsiboPenaltySummary(User isiboLeader) {
        Isibo isibo = isiboLeader.getIsibo();
        if (isibo == null)
            throw new RuntimeException("You are not assigned to any isibo.");

        List<PenaltyFlag> allFlags = penaltyFlagRepository.findByCitizenIsiboId(isibo.getId());

        Map<UUID, List<PenaltyFlag>> byCitizen = allFlags.stream()
                .collect(Collectors.groupingBy(f -> f.getCitizen().getId()));

        List<HouseholdPenaltySummary> households = byCitizen.entrySet().stream()
                .map(entry -> {
                    List<PenaltyFlag> flags = entry.getValue();
                    User citizen = flags.get(0).getCitizen();

                    List<PenaltyFlag> unpaid = flags.stream()
                            .filter(f -> f.getStatus() == PenaltyStatus.UNPAID)
                            .collect(Collectors.toList());

                    BigDecimal outstanding = unpaid.stream()
                            .map(f -> f.getAmount() != null ? f.getAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<PenaltyResponse> penaltyResponses = flags.stream()
                            .filter(f -> f.getStatus() == PenaltyStatus.UNPAID
                                    || f.getStatus() == PenaltyStatus.PAID)
                            .map(f -> toResponse(f, resolveActivityTitle(f.getActivityId())))
                            .collect(Collectors.toList());

                    return HouseholdPenaltySummary.builder()
                            .citizenId(citizen.getId())
                            .citizenFullName(citizen.getFirstName() + " " + citizen.getLastName())
                            .unpaidCount(unpaid.size())
                            .totalOutstanding(outstanding)
                            .penalties(penaltyResponses)
                            .build();
                })
                .collect(Collectors.toList());

        int householdsWithUnpaid = (int) households.stream()
                .filter(h -> h.getUnpaidCount() > 0).count();

        BigDecimal totalOutstanding = households.stream()
                .map(HouseholdPenaltySummary::getTotalOutstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalUnpaid = households.stream()
                .mapToInt(HouseholdPenaltySummary::getUnpaidCount).sum();

        return IsiboPenaltySummaryResponse.builder()
                .isiboId(isibo.getId())
                .isiboName(isibo.getName())
                .totalHouseholds(isibo.getCitizens() != null ? isibo.getCitizens().size() : 0)
                .householdsWithUnpaid(householdsWithUnpaid)
                .totalOutstandingAmount(totalOutstanding)
                .totalUnpaidCount(totalUnpaid)
                .households(households)
                .build();
    }


    private PenaltyFlag findFlagOrThrow(UUID flagId) {
        return penaltyFlagRepository.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Penalty flag not found: " + flagId));
    }

    private String resolveActivityTitle(UUID activityId) {
        return activityRepository.findById(activityId)
                .map(Activity::getTitle)
                .orElse("Unknown activity");
    }

    private void sendNotification(User recipient, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    private void sendSms(User citizen, String message) {
        if (citizen.getPhoneNumber() != null) {
            smsService.sendSms(citizen.getPhoneNumber(), message);
        }
    }

    private PenaltyResponse toResponse(PenaltyFlag f, String activityTitle) {
        return PenaltyResponse.builder()
                .id(f.getId())
                .activityId(f.getActivityId())
                .activityTitle(activityTitle)
                .citizenId(f.getCitizen().getId())
                .citizenFullName(f.getCitizen().getFirstName() + " " + f.getCitizen().getLastName())
                .status(f.getStatus())
                .amount(f.getAmount())
                .reason(f.getReason())
                .dueDate(f.getDueDate())
                .paidAt(f.getPaidAt())
                .exemptionReason(f.getExemptionReason())
                .reviewedByFullName(f.getReviewedBy() != null
                        ? f.getReviewedBy().getFirstName() + " " + f.getReviewedBy().getLastName()
                        : null)
                .flaggedAt(f.getFlaggedAt())
                .reviewedAt(f.getReviewedAt())
                .build();
    }

    private PenaltyFlagResponse toLegacyResponse(PenaltyFlag f) {
        PenaltyFlagResponse r = new PenaltyFlagResponse();
        r.setId(f.getId());
        r.setActivityId(f.getActivityId());
        r.setCitizenId(f.getCitizen().getId());
        r.setCitizenFullName(f.getCitizen().getFirstName() + " " + f.getCitizen().getLastName());
        r.setAttendanceId(f.getAttendance().getId());
        r.setStatus(f.getStatus());
        r.setReviewNote(f.getReviewNote());
        if (f.getReviewedBy() != null)
            r.setReviewedByFullName(f.getReviewedBy().getFirstName() + " " + f.getReviewedBy().getLastName());
        r.setFlaggedAt(f.getFlaggedAt());
        r.setReviewedAt(f.getReviewedAt());
        return r;
    }
}