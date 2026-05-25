package com.umudugudu.service.impl;

import com.umudugudu.dto.request.ReviewServiceRequestDto;
import com.umudugudu.dto.request.SubmitServiceRequestDto;
import com.umudugudu.dto.response.ServiceRequestResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.NotificationRepository;
import com.umudugudu.repository.ServiceRequestRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.ServiceRequestService;
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
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final NotificationRepository   notificationRepository;
    private final UserRepository           userRepository;

    @Override
    @Transactional
    public ServiceRequestResponse submit(SubmitServiceRequestDto dto, User citizen) {

        // Validation: description is already enforced by @NotBlank, but guard anyway
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        ServiceRequest request = ServiceRequest.builder()
                .citizen(citizen)
                .requestType(dto.getRequestType())
                .description(dto.getDescription().trim())
                .villageId(citizen.getVillage() != null ? citizen.getVillage().getId() : null)
                .isiboId(citizen.getIsibo()   != null ? citizen.getIsibo().getId()   : null)
                .build();

        ServiceRequest saved = serviceRequestRepository.save(request);
        log.info("Service request {} submitted by citizen {}", saved.getId(), citizen.getId());

        // Notify the relevant leader
        notifyLeaderOnSubmission(saved, citizen);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public ServiceRequestResponse review(UUID requestId,
                                         ReviewServiceRequestDto dto,
                                         User leader) {

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found: " + requestId));

        // Only PENDING or INFO_REQUIRED requests can be actioned
        if (request.getStatus() == ServiceRequestStatus.APPROVED
                || request.getStatus() == ServiceRequestStatus.REJECTED) {
            throw new RuntimeException("This request has already been finalised.");
        }

        // Decision must be one of the three allowed outcomes
        ServiceRequestStatus decision = dto.getDecision();
        if (decision != ServiceRequestStatus.APPROVED
                && decision != ServiceRequestStatus.REJECTED
                && decision != ServiceRequestStatus.INFO_REQUIRED) {
            throw new IllegalArgumentException(
                    "Decision must be APPROVED, REJECTED, or INFO_REQUIRED.");
        }

        // Response required when rejecting or requesting more info
        if ((decision == ServiceRequestStatus.REJECTED
                || decision == ServiceRequestStatus.INFO_REQUIRED)
                && (dto.getLeaderResponse() == null || dto.getLeaderResponse().isBlank())) {
            throw new IllegalArgumentException(
                    "A leader response is required when rejecting or requesting more information.");
        }

        // Scope check: leader must belong to the same village as the request
        if (leader.getVillage() == null
                || !leader.getVillage().getId().equals(request.getVillageId())) {
            throw new RuntimeException(
                    "You are not authorised to review requests outside your village.");
        }

        request.setStatus(decision);
        request.setLeaderResponse(dto.getLeaderResponse());
        request.setReviewedBy(leader);
        request.setReviewedAt(LocalDateTime.now());

        ServiceRequest saved = serviceRequestRepository.save(request);
        log.info("Service request {} reviewed by {} — decision: {}",
                requestId, leader.getId(), decision);

        // Notify the citizen of the outcome
        notifyCitizenOnReview(saved);

        return toResponse(saved);
    }

    @Override
    public List<ServiceRequestResponse> getMyRequests(User citizen) {
        return serviceRequestRepository
                .findByCitizenIdOrderByCreatedAtDesc(citizen.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRequestResponse> getPendingQueue(User leader) {
        UUID villageId = resolveVillageId(leader);

        if (leader.getRole() == Role.ISIBO_LEADER && leader.getIsibo() != null) {
            // Isibo leader only sees requests from their isibo
            return serviceRequestRepository
                    .findByIsiboIdAndStatusOrderByCreatedAtAsc(
                            leader.getIsibo().getId(), ServiceRequestStatus.PENDING)
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }

        return serviceRequestRepository
                .findByVillageIdAndStatusOrderByCreatedAtAsc(villageId, ServiceRequestStatus.PENDING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ServiceRequestResponse> getFullQueue(User leader) {
        UUID villageId = resolveVillageId(leader);
        return serviceRequestRepository
                .findByVillageIdOrderByCreatedAtDesc(villageId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }


    /**
     * Notify the relevant leader when a citizen submits a new request.
     * Priority: Isibo Leader → Village Leader (fallback).
     */
    private void notifyLeaderOnSubmission(ServiceRequest request, User citizen) {

        User recipient = resolveLeaderForCitizen(citizen);
        if (recipient == null) {
            log.warn("No leader found for citizen {} — submission notification skipped",
                    citizen.getId());
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(String.format(
                "%s %s submitted a %s service request and is awaiting your review.",
                citizen.getFirstName(),
                citizen.getLastName(),
                request.getRequestType().name().replace("_", " ")
        ));
        notificationRepository.save(notification);

        log.info("Notification sent to leader {} for service request {}",
                recipient.getId(), request.getId());
    }

    /**
     * Notify the citizen when their request has been reviewed.
     */
    private void notifyCitizenOnReview(ServiceRequest request) {
        String message;
        switch (request.getStatus()) {
            case APPROVED -> message = String.format(
                    "Your %s service request has been APPROVED.%s",
                    request.getRequestType().name().replace("_", " "),
                    request.getLeaderResponse() != null
                            ? " Leader's note: " + request.getLeaderResponse()
                            : "");
            case REJECTED -> message = String.format(
                    "Your %s service request has been REJECTED. Reason: %s",
                    request.getRequestType().name().replace("_", " "),
                    request.getLeaderResponse());
            case INFO_REQUIRED -> message = String.format(
                    "Your %s service request requires more information. Leader's message: %s",
                    request.getRequestType().name().replace("_", " "),
                    request.getLeaderResponse());
            default -> message = "Your service request status has been updated to: "
                    + request.getStatus().name();
        }

        Notification notification = new Notification();
        notification.setRecipient(request.getCitizen());
        notification.setMessage(message);
        notificationRepository.save(notification);

        log.info("Citizen {} notified about service request {} status: {}",
                request.getCitizen().getId(), request.getId(), request.getStatus());
    }

    /**
     * Resolve the best leader to notify for a given citizen:
     * Isibo Leader if available, otherwise Village Leader.
     */
    private User resolveLeaderForCitizen(User citizen) {
        // 1. Try the isibo leader
        if (citizen.getIsibo() != null && citizen.getIsibo().getIsiboLeader() != null) {
            return citizen.getIsibo().getIsiboLeader();
        }
        // 2. Fallback to village leader
        if (citizen.getVillage() != null && citizen.getVillage().getVillageLeader() != null) {
            return citizen.getVillage().getVillageLeader();
        }
        return null;
    }

    private UUID resolveVillageId(User leader) {
        if (leader.getVillage() == null) {
            throw new RuntimeException("You are not assigned to any village.");
        }
        return leader.getVillage().getId();
    }

    private ServiceRequestResponse toResponse(ServiceRequest r) {
        return ServiceRequestResponse.builder()
                .id(r.getId())
                .citizenId(r.getCitizen().getId())
                .citizenFullName(r.getCitizen().getFirstName() + " " + r.getCitizen().getLastName())
                .citizenPhone(r.getCitizen().getPhoneNumber())
                .requestType(r.getRequestType())
                .description(r.getDescription())
                .status(r.getStatus())
                .leaderResponse(r.getLeaderResponse())
                .reviewedByFullName(r.getReviewedBy() != null
                        ? r.getReviewedBy().getFirstName() + " " + r.getReviewedBy().getLastName()
                        : null)
                .reviewedAt(r.getReviewedAt())
                .createdAt(r.getCreatedAt())
                .villageId(r.getVillageId())
                .isiboId(r.getIsiboId())
                .build();
    }
}
