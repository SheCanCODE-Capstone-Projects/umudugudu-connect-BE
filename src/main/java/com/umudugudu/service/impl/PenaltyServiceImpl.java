package com.umudugudu.service.impl;

import com.umudugudu.dto.request.AssignPenaltyRequest;
import com.umudugudu.dto.response.PenaltyResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.AttendanceRepository;
import com.umudugudu.repository.ExemptionRepository;
import com.umudugudu.repository.PenaltyRepository;
import com.umudugudu.service.NotificationService;
import com.umudugudu.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExemptionRepository exemptionRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void generatePenaltyIfNeeded(Attendance attendance) {

        if (attendance.getStatus() != AttendanceStatus.ABSENT) {
            return;
        }

        boolean exists =
                penaltyRepository.existsByAttendance_Id(attendance.getId());

        if (exists) {
            return;
        }

        Penalty penalty = Penalty.builder()
                .attendance(attendance)
                .citizen(attendance.getCitizen())
                .amount(java.math.BigDecimal.valueOf(500))
                .reason("Absence without exemption")
                .assignedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .paymentStatus(PenaltyPaymentStatus.UNPAID)
                .build();

        penaltyRepository.save(penalty);
    }

    @Override
    @Transactional
    public PenaltyResponse assignPenalty(AssignPenaltyRequest request) {

        Attendance attendance = attendanceRepository
                .findById(request.attendanceId())
                .orElseThrow(() ->
                        new RuntimeException("Attendance not found"));

        if (attendance.getStatus() != AttendanceStatus.ABSENT) {
            throw new RuntimeException(
                    "Penalty can only be assigned to absent citizens");
        }

        boolean exists = penaltyRepository
                .existsByAttendance_Id(attendance.getId());

        if (exists) {
            throw new RuntimeException(
                    "Penalty already exists");
        }

        if (Boolean.TRUE.equals(request.exemption())) {
            throw new RuntimeException(
                    "Citizen has exemption. Penalty not created");
        }

        Penalty penalty = Penalty.builder()
                .attendance(attendance)
                .citizen(attendance.getCitizen())
                .amount(request.amount())
                .reason(request.reason())
                .assignedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .paymentStatus(PenaltyPaymentStatus.UNPAID)
                .build();

        Penalty saved = penaltyRepository.save(penalty);

        eventPublisher.publishEvent(
                new PenaltyCreatedEvent(
                        attendance.getCitizen(),
                        saved.getAmount(),
                        saved.getReason()
                )
        );

        return new PenaltyResponse(
                saved.getId(),
                saved.getAmount(),
                saved.getReason(),
                saved.getPaymentStatus().name()
        );
    }
}