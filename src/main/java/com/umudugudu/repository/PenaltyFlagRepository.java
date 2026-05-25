package com.umudugudu.repository;

import com.umudugudu.entity.PenaltyFlag;
import com.umudugudu.entity.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyFlagRepository extends JpaRepository<PenaltyFlag, UUID> {

    List<PenaltyFlag> findByActivityId(UUID activityId);

    List<PenaltyFlag> findByActivityIdAndReviewStatus(UUID activityId, PenaltyStatus reviewStatus);

    List<PenaltyFlag> findByCitizenId(UUID citizenId);

    Optional<PenaltyFlag> findByActivityIdAndCitizenId(UUID activityId, UUID citizenId);

    List<PenaltyFlag> findByCitizen_IsiboId(UUID isiboId);

    List<PenaltyFlag> findByCitizen_IsiboIdAndReviewStatus(UUID isiboId, PenaltyStatus reviewStatus);
}