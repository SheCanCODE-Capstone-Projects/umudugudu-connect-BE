package com.umudugudu.service.impl;
import com.umudugudu.dto.*;
import com.umudugudu.dto.request.ProfileChangeRequestDto;
import com.umudugudu.dto.request.ReviewChangeRequestDto;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.entity.*;
import com.umudugudu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umudugudu.dto.response.ProfileResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileChangeRequestRepository changeRequestRepository;
    private final NotificationRepository notificationRepository;
    private final VillageRepository villageRepository;

    //VIEW PROFILE

    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().name())
                .villageName(user.getVillage() != null ? user.getVillage().getName() : "Not assigned")
                .isiboName(user.getIsibo() != null ? user.getIsibo().getName() : "Not assigned")
                .build();
    }

    //SUBMIT CHANGE REQUEST

    @Transactional
    public ChangeRequestResponse submitChangeRequest(Long userId, ProfileChangeRequestDto dto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent duplicate pending requests
        boolean hasPending = changeRequestRepository
                .existsByRequesterIdAndStatus(userId, RequestStatus.PENDING);
        if (hasPending) {
            throw new RuntimeException("You already have a pending change request. Please wait for it to be reviewed.");
        }

        // Save change request
        ProfileChangeRequest request = new ProfileChangeRequest();
        request.setRequester(requester);
        request.setRequestedFirstName(dto.getFirstName());
        request.setRequestedLastName(dto.getLastName());
        request.setRequestedPhoneNumber(dto.getPhoneNumber());
        ProfileChangeRequest saved = changeRequestRepository.save(request);

        // Notify the Village Leader
        notifyVillageLeader(requester, saved);

        return toChangeRequestResponse(saved);
    }

    //VILLAGE LEADER: VIEW PENDING REQUESTS

    public List<ChangeRequestResponse> getPendingRequests() {
        return changeRequestRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(this::toChangeRequestResponse)
                .collect(Collectors.toList());
    }

    //VILLAGE LEADER: APPROVE OR REJECT

    @Transactional
    public ChangeRequestResponse reviewChangeRequest(Long requestId, ReviewChangeRequestDto dto) {
        ProfileChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Change request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("This request has already been reviewed.");
        }

        if (dto.isApproved()) {
            applyChanges(request);
            request.setStatus(RequestStatus.APPROVED);
        } else {
            if (dto.getRejectionReason() == null || dto.getRejectionReason().isBlank()) {
                throw new RuntimeException("A rejection reason is required.");
            }
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(dto.getRejectionReason());
        }

        request.setResolvedAt(LocalDateTime.now());
        ProfileChangeRequest saved = changeRequestRepository.save(request);

        // Notify the citizen of the outcome
        notifyCitizen(saved);

        return toChangeRequestResponse(saved);
    }

    //CITIZEN: VIEW OWN REQUESTS

    public List<ChangeRequestResponse> getMyRequests(Long userId) {
        return changeRequestRepository.findByRequesterId(userId)
                .stream()
                .map(this::toChangeRequestResponse)
                .collect(Collectors.toList());
    }

    // VILLAGE LEADER: GET UNREAD NOTIFICATIONS

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public void markNotificationRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // PRIVATE HELPERS

    private void applyChanges(ProfileChangeRequest request) {
        User user = request.getRequester();
        if (request.getRequestedFirstName() != null)
            user.setFirstName(request.getRequestedFirstName());
        if (request.getRequestedLastName() != null)
            user.setLastName(request.getRequestedLastName());
        if (request.getRequestedPhoneNumber() != null)
            user.setPhoneNumber(request.getRequestedPhoneNumber());
        userRepository.save(user);
    }

    private void notifyVillageLeader(User requester, ProfileChangeRequest request) {
        // Find the Village Leader for the citizen's village
        if (requester.getVillage() == null || requester.getVillage().getVillageLeader() == null) return;

        User villageLeader = requester.getVillage().getVillageLeader();

        Notification notification = new Notification();
        notification.setRecipient(villageLeader);
        notification.setChangeRequest(request);
        notification.setMessage(String.format(
                "%s %s has submitted a profile change request and is awaiting your review.",
                requester.getFirstName(), requester.getLastName()
        ));
        notificationRepository.save(notification);
    }

    private void notifyCitizen(ProfileChangeRequest request) {
        Notification notification = new Notification();
        notification.setRecipient(request.getRequester());
        notification.setChangeRequest(request);

        if (request.getStatus() == RequestStatus.APPROVED) {
            notification.setMessage("Your profile change request has been approved. Your profile has been updated.");
        } else {
            notification.setMessage(String.format(
                    "Your profile change request was rejected. Reason: %s", request.getRejectionReason()
            ));
        }
        notificationRepository.save(notification);
    }

    private ChangeRequestResponse toChangeRequestResponse(ProfileChangeRequest r) {
        return ChangeRequestResponse.builder()
                .id(r.getId())
                .requesterName(r.getRequester().getFirstName() + " " + r.getRequester().getLastName())
                .requestedFirstName(r.getRequestedFirstName())
                .requestedLastName(r.getRequestedLastName())
                .requestedPhoneNumber(r.getRequestedPhoneNumber())
                .status(r.getStatus().name())
                .createdAt(r.getCreatedAt())
                .resolvedAt(r.getResolvedAt())
                .rejectionReason(r.getRejectionReason())
                .build();
    }
}
