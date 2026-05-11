package com.umudugudu.repository;

import com.umudugudu.entity.PenaltyFlag;
import com.umudugudu.entity.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyFlagRepository extends JpaRepository<PenaltyFlag, Long> {
    List<PenaltyFlag> findByActivityId(UUID activityId);
    List<PenaltyFlag> findByActivityIdAndStatus(UUID activityId, PenaltyStatus status);
    List<PenaltyFlag> findByCitizenId(Long citizenId);
    Optional<PenaltyFlag> findByActivityIdAndCitizenId(UUID activityId, Long citizenId);
}