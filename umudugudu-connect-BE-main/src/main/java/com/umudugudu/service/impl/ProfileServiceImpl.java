package com.umudugudu.service.impl;

import com.umudugudu.dto.request.ProfileChangeRequestDTO;
import com.umudugudu.dto.request.ReviewChangeRequestDTO;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import com.umudugudu.entity.*;
import com.umudugudu.exception.BusinessException;
import com.umudugudu.exception.ResourceNotFoundException;
import com.umudugudu.repository.ProfileChangeRequestRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileChangeRequestRepository changeRequestRepository;
    private final EmailService emailService;

    private User resolveUser(String username) {
        return userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    public ProfileResponse getMyProfile(String username) {
        return new ProfileResponse(resolveUser(username));
    }

    @Override
    @Transactional
    public ChangeRequestResponse submitChangeRequest(String username, ProfileChangeRequestDTO dto) {
        User user = resolveUser(username);

        if (changeRequestRepository.existsByUserAndStatus(user, ChangeRequestStatus.PENDING)) {
            throw new BusinessException("You already have a pending change request. Please wait for the Village Leader to review it.");
        }
        if (allFieldsNull(dto)) {
            throw new BusinessException("At least one field must be provided for a change request.");
        }

        ProfileChangeRequest request = new ProfileChangeRequest();
        request.setUser(user);
        request.setRequestedFirstName(dto.getFirstName());
        request.setRequestedLastName(dto.getLastName());
        request.setRequestedPhoneNumber(dto.getPhoneNumber());
        request.setRequestedVillage(dto.getVillage());
        request.setRequestedIsibo(dto.getIsibo());
        request.setStatus(ChangeRequestStatus.PENDING);

        ProfileChangeRequest saved = changeRequestRepository.save(request);
        notifyVillageLeaders(user, saved);

        return new ChangeRequestResponse(saved);
    }

    @Override
    public List<ChangeRequestResponse> getMyChangeRequests(String username) {
        User user = resolveUser(username);
        return changeRequestRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(ChangeRequestResponse::new).collect(Collectors.toList());
    }

    @Override
    public List<ChangeRequestResponse> getPendingChangeRequests(String leaderUsername) {
        assertVillageLeader(resolveUser(leaderUsername));
        return changeRequestRepository.findByStatusOrderByCreatedAtAsc(ChangeRequestStatus.PENDING)
                .stream().map(ChangeRequestResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChangeRequestResponse reviewChangeRequest(String leaderUsername, Long requestId, ReviewChangeRequestDTO dto) {
        User leader = resolveUser(leaderUsername);
        assertVillageLeader(leader);

        ProfileChangeRequest changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request not found: " + requestId));

        if (changeRequest.getStatus() != ChangeRequestStatus.PENDING) {
            throw new BusinessException("This change request has already been reviewed.");
        }

        changeRequest.setReviewedBy(leader);
        changeRequest.setReviewedAt(LocalDateTime.now());

        if (dto.isApproved()) {
            applyChanges(changeRequest);
            changeRequest.setStatus(ChangeRequestStatus.APPROVED);
            notifyUserOfDecision(changeRequest, true, null);
        } else {
            if (dto.getRejectionReason() == null || dto.getRejectionReason().isBlank()) {
                throw new BusinessException("A rejection reason is required.");
            }
            changeRequest.setStatus(ChangeRequestStatus.REJECTED);
            changeRequest.setRejectionReason(dto.getRejectionReason());
            notifyUserOfDecision(changeRequest, false, dto.getRejectionReason());
        }

        return new ChangeRequestResponse(changeRequestRepository.save(changeRequest));
    }

    private void applyChanges(ProfileChangeRequest req) {
        User user = req.getUser();
        if (req.getRequestedFirstName()   != null) user.setFirstName(req.getRequestedFirstName());
        if (req.getRequestedLastName()    != null) user.setLastName(req.getRequestedLastName());
        if (req.getRequestedPhoneNumber() != null) user.setPhoneNumber(req.getRequestedPhoneNumber());
        if (req.getRequestedVillage()     != null) user.setVillage(req.getRequestedVillage());
        if (req.getRequestedIsibo()       != null) user.setIsibo(req.getRequestedIsibo());
        userRepository.save(user);
    }

    private void assertVillageLeader(User user) {
        if (user.getRole() != Role.VILLAGE_LEADER && user.getRole() != Role.ADMIN) {
            throw new BusinessException("Only a Village Leader or Admin can perform this action.");
        }
    }

    private boolean allFieldsNull(ProfileChangeRequestDTO dto) {
        return dto.getFirstName() == null && dto.getLastName() == null
                && dto.getPhoneNumber() == null && dto.getVillage() == null && dto.getIsibo() == null;
    }

    private void notifyVillageLeaders(User requestingUser, ProfileChangeRequest req) {
        List<User> leaders = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.VILLAGE_LEADER && u.getEmail() != null)
                .collect(Collectors.toList());

        String name = requestingUser.getFirstName() + " " + requestingUser.getLastName();
        String subject = "New Profile Change Request — " + name;
        String body = String.format(
                "Hello Village Leader,\n\nUser %s (ID: %d) submitted profile change request #%d.\n\n" +
                        "Requested changes:\n  First name: %s\n  Last name: %s\n  Phone: %s\n  Village: %s\n  Isibo: %s\n\n" +
                        "Please log in to approve or reject.\n\nUmudugudu Connect",
                name, requestingUser.getId(), req.getId(),
                nvl(req.getRequestedFirstName()), nvl(req.getRequestedLastName()),
                nvl(req.getRequestedPhoneNumber()), nvl(req.getRequestedVillage()), nvl(req.getRequestedIsibo()));

        for (User leader : leaders) {
            try { emailService.sendGenericEmail(leader.getEmail(), subject, body); }
            catch (Exception e) { log.warn("Could not notify leader {} — {}", leader.getEmail(), e.getMessage()); }
        }
    }

    private void notifyUserOfDecision(ProfileChangeRequest req, boolean approved, String reason) {
        User user = req.getUser();
        if (user.getEmail() == null) return;
        String subject = approved ? "Your Profile Change Request Has Been Approved"
                : "Your Profile Change Request Has Been Rejected";
        String body = approved
                ? String.format("Hello %s,\n\nYour change request (#%d) was approved and your profile has been updated.\n\nUmudugudu Connect", user.getFirstName(), req.getId())
                : String.format("Hello %s,\n\nYour change request (#%d) was rejected.\n\nReason: %s\n\nUmudugudu Connect", user.getFirstName(), req.getId(), reason);
        try { emailService.sendGenericEmail(user.getEmail(), subject, body); }
        catch (Exception e) { log.warn("Could not notify user {} — {}", user.getEmail(), e.getMessage()); }
    }

    private String nvl(String v) { return v != null ? v : "(no change)"; }
}