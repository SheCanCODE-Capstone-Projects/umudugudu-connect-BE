package com.umudugudu.service;

import com.umudugudu.dto.request.ChangeRequestReviewRequest;
import com.umudugudu.dto.request.ChangeRequestSubmitRequest;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import com.umudugudu.entity.ChangeRequestStatus;
import com.umudugudu.entity.ProfileChangeRequest;
import com.umudugudu.entity.User;
import com.umudugudu.exception.BusinessException;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.repository.ProfileChangeRequestRepository;
import com.umudugudu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository                 userRepository;
    private final ProfileChangeRequestRepository changeRequestRepository;

    // ── View own profile ─────────────────────────────────────────────────────

    public ProfileResponse getProfile(String email) {
        User user = findUserByEmail(email);
        return toProfileResponse(user);
    }

    // ── Citizen: submit a change request ─────────────────────────────────────

    @Transactional
    public ChangeRequestResponse submitChangeRequest(String email,
                                                     ChangeRequestSubmitRequest request) {
        User user = findUserByEmail(email);

        if (changeRequestRepository.existsByUserIdAndStatus(
                user.getId(), ChangeRequestStatus.PENDING)) {
            throw new BusinessException(
                    "You already have a pending change request. " +
                            "Wait for it to be reviewed before submitting a new one.");
        }

        if (request.getFullName()  == null &&
                request.getEmail()     == null &&
                request.getVillageId() == null &&
                request.getIsiboId()   == null) {
            throw new BusinessException("Please specify at least one field to change.");
        }

        ProfileChangeRequest changeRequest = new ProfileChangeRequest();
        changeRequest.setUser(user);
        changeRequest.setRequestedFullName(request.getFullName());
        changeRequest.setRequestedEmail(request.getEmail());
        changeRequest.setRequestedVillageId(request.getVillageId());
        changeRequest.setRequestedIsiboId(request.getIsiboId());
        changeRequest.setCitizenNote(request.getNote());
        changeRequest.setStatus(ChangeRequestStatus.PENDING);

        ProfileChangeRequest saved = changeRequestRepository.save(changeRequest);

        // TODO: EmailService.notifyVillageLeader(user.getVillageId(), saved)

        return toChangeRequestResponse(saved);
    }

    // ── Village Leader: pending review queue ─────────────────────────────────

    public Page<ChangeRequestResponse> getPendingRequests(
            String leaderEmail, int page, int size) {
        User leader = findUserByEmail(leaderEmail);

        if (leader.getVillageId() == null) {
            throw new BusinessException("Your account is not associated with a village.");
        }

        return changeRequestRepository
                .findByStatusAndUser_VillageId(
                        ChangeRequestStatus.PENDING,
                        leader.getVillageId(),
                        PageRequest.of(page, size))
                .map(this::toChangeRequestResponse);
    }

    // ── Village Leader: approve or reject ────────────────────────────────────

    @Transactional
    public ChangeRequestResponse reviewChangeRequest(UUID requestId,
                                                     String leaderEmail,
                                                     ChangeRequestReviewRequest review) {
        User leader = findUserByEmail(leaderEmail);

        ProfileChangeRequest changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProfileChangeRequest", requestId));

        if (changeRequest.getStatus() != ChangeRequestStatus.PENDING) {
            throw new BusinessException("This request has already been reviewed.");
        }

        User citizen = changeRequest.getUser();
        if (!leader.getVillageId().equals(citizen.getVillageId())) {
            throw new BusinessException(
                    "You can only review requests from your own village.");
        }

        if (review.getDecision() == ChangeRequestStatus.REJECTED &&
                (review.getResponse() == null || review.getResponse().isBlank())) {
            throw new BusinessException(
                    "Please provide a reason when rejecting a request.");
        }

        changeRequest.setStatus(review.getDecision());
        changeRequest.setLeaderResponse(review.getResponse());
        changeRequest.setReviewedBy(leader);
        changeRequest.setReviewedAt(LocalDateTime.now());

        if (review.getDecision() == ChangeRequestStatus.APPROVED) {
            applyChangesToUser(citizen, changeRequest);
            userRepository.save(citizen);
        }

        ProfileChangeRequest saved = changeRequestRepository.save(changeRequest);

        // TODO: EmailService.notifyCitizen(citizen.getEmail(), review.getDecision(), review.getResponse())

        return toChangeRequestResponse(saved);
    }

    // ── Citizen: own request history ─────────────────────────────────────────

    public List<ChangeRequestResponse> myChangeRequests(String email) {
        User user = findUserByEmail(email);
        return changeRequestRepository.findByUserId(user.getId())
                .stream()
                .map(this::toChangeRequestResponse)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private void applyChangesToUser(User user, ProfileChangeRequest req) {
        if (req.getRequestedFullName()  != null) user.setFullName(req.getRequestedFullName());
        if (req.getRequestedEmail()     != null) user.setEmail(req.getRequestedEmail());
        if (req.getRequestedVillageId() != null) user.setVillageId(req.getRequestedVillageId());
        if (req.getRequestedIsiboId()   != null) user.setIsiboId(req.getRequestedIsiboId());
        user.setUpdatedAt(LocalDateTime.now());
    }

    private ProfileResponse toProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .villageId(user.getVillageId())
                .isiboId(user.getIsiboId())
                .active(user.isActive())
                .build();
    }
    private ChangeRequestResponse toChangeRequestResponse(ProfileChangeRequest req) {
        return ChangeRequestResponse.builder()
                .id(req.getId())
                .userId(req.getUser().getId())
                .userFullName(req.getUser().getFullName())
                .requestedFullName(req.getRequestedFullName())
                .requestedEmail(req.getRequestedEmail())
                .requestedVillageId(req.getRequestedVillageId())
                .requestedIsiboId(req.getRequestedIsiboId())
                .citizenNote(req.getCitizenNote())
                .status(req.getStatus())
                .leaderResponse(req.getLeaderResponse())
                .createdAt(req.getCreatedAt())
                .reviewedAt(req.getReviewedAt())
                .build();
    }
}