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
<<<<<<< HEAD
    List<PenaltyFlag> findByActivityIdAndStatus(UUID activityId, PenaltyStatus status);
    List<PenaltyFlag> findByCitizenId(UUID citizenId);
=======

    List<PenaltyFlag> findByActivityIdAndReviewStatus(UUID activityId, PenaltyStatus reviewStatus);

    List<PenaltyFlag> findByCitizenId(UUID citizenId);

>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)
    Optional<PenaltyFlag> findByActivityIdAndCitizenId(UUID activityId, UUID citizenId);

    // Find all penalties for citizens in a specific isibo
    List<PenaltyFlag> findByCitizen_IsiboId(UUID isiboId);

    // Find unflagged/unresolved penalties for citizens in a specific isibo
    List<PenaltyFlag> findByCitizen_IsiboIdAndStatus(UUID isiboId, PenaltyStatus status);
}