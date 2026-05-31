package com.umudugudu.service.impl;

import com.umudugudu.dto.response.AuditLogResponse;
import com.umudugudu.dto.response.DashboardResponse;
import com.umudugudu.dto.response.UserResponseDTO;
import com.umudugudu.entity.*;
import com.umudugudu.repository.*;
import com.umudugudu.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VillageRepository villageRepository;
    private final ActivityRepository activityRepository;
    private final PenaltyFlagRepository penaltyFlagRepository;
    private final AuditLogRepository auditLogRepository;


    @Override
    @Transactional
    public String updateRoleByEmail(String email, Role newRole) {
        if (email == null || email.isEmpty()) return "Email is required";
        if (newRole == null) return "Role is required";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole().equals(newRole)) {
            return "User already has role: " + user.getRole();
        }

        user.setRole(newRole);
        userRepository.save(user);
        return "Role updated to " + newRole + " for user " + email;
    }

    @Override
    public UserResponseDTO findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toUserDto(user);
    }

    @Override
    @Transactional
    public String assignVillageLeader(String email, UUID villageId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new RuntimeException("Village not found: " + villageId));

        user.setRole(Role.VILLAGE_LEADER);
        user.setVillage(village);
        userRepository.save(user);
        return "User " + email + " assigned as village leader for " + village.getName();
    }

    @Override
    @Transactional
    public String createVillage(String name) {
        if (name == null || name.trim().isEmpty()) return "Village name is required";
        if (villageRepository.existsByName(name)) return "Village '" + name + "' already exists";

        Village village = new Village();
        village.setName(name);
        villageRepository.save(village);
        return "Village '" + name + "' created successfully";
    }

    //Dashboard

    @Override
    public DashboardResponse getDashboardStats() {
        long totalUsers      = userRepository.count();
        long totalVillages   = villageRepository.count();
        long totalActivities = activityRepository.count();
        long totalPenalties  = penaltyFlagRepository.count();

        long confirmed = penaltyFlagRepository.countByReviewStatus(PenaltyStatus.CONFIRMED);
        long waived    = penaltyFlagRepository.countByReviewStatus(PenaltyStatus.WAIVED);
        long flagged   = penaltyFlagRepository.countByReviewStatus(PenaltyStatus.FLAGGED);

        long totalAuditLogs = auditLogRepository.count();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalVillages(totalVillages)
                .totalActivities(totalActivities)
                .totalPenalties(totalPenalties)
                .confirmedPenalties(confirmed)
                .waivedPenalties(waived)
                .flaggedPenalties(flagged)
                .totalAuditLogs(totalAuditLogs)
                .build();
    }

    //User Management

    @Override
    public Page<UserResponseDTO> getAllUsers(String village, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());

        if (village != null && !village.isEmpty() && role != null && !role.isEmpty()) {
            Village v = villageRepository.findByName(village)
                    .orElseThrow(() -> new RuntimeException("Village not found: " + village));
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return userRepository.findByVillageAndRole(v, roleEnum, pageable)
                    .map(this::toUserDto);
        }

        if (village != null && !village.isEmpty()) {
            Village v = villageRepository.findByName(village)
                    .orElseThrow(() -> new RuntimeException("Village not found: " + village));
            return userRepository.findByVillage(v, pageable)
                    .map(this::toUserDto);
        }

        if (role != null && !role.isEmpty()) {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return userRepository.findByRole(roleEnum, pageable)
                    .map(this::toUserDto);
        }

        return userRepository.findAll(pageable).map(this::toUserDto);
    }

    @Override
    @Transactional
    public String deactivateUser(UUID userId, User performedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (!user.isEnabled()) {
            return "User is already deactivated";
        }

        user.setEnabled(false);
        userRepository.save(user);

        // US-7.3 — write audit log
        AuditLog auditLog = AuditLog.builder()
                .performedBy(performedBy)
                .action("USER_DEACTIVATED")
                .entityType("User")
                .entityId(userId)
                .oldValue("enabled=true")
                .newValue("enabled=false")
                .build();
        auditLogRepository.save(auditLog);

        log.info("User {} deactivated by {}", user.getEmail(), performedBy.getEmail());
        return "User " + user.getEmail() + " deactivated successfully";
    }

    //Audit Logs

    @Override
    public Page<AuditLogResponse> getAuditLogs(String userId, String action, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("performedAt").descending());

        // Issue 5 fix — guard against malformed userId
        if (userId != null) {
            try {
                UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid userId format: " + userId);
            }
        }

        Page<AuditLog> logs;

        if (userId != null && action != null) {
            logs = auditLogRepository.findByPerformedBy_IdAndAction(
                    UUID.fromString(userId), action, pageable);
        } else if (userId != null) {
            logs = auditLogRepository.findByPerformedBy_Id(
                    UUID.fromString(userId), pageable);
        } else if (action != null) {
            logs = auditLogRepository.findByAction(action, pageable);
        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        return logs.map(this::toAuditLogResponse);
    }

    //Private helpers

    private UserResponseDTO toUserDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private AuditLogResponse toAuditLogResponse(AuditLog log) {
        String performedBy = log.getPerformedBy() != null
                ? log.getPerformedBy().getFirstName() + " "
                  + log.getPerformedBy().getLastName()
                : "System";

        return AuditLogResponse.builder()
                .id(log.getId())
                .performedBy(performedBy)
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .performedAt(log.getPerformedAt())
                .build();
    }
}