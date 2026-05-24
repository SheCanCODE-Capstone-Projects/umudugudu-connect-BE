package com.umudugudu.repository;

import com.umudugudu.entity.PenaltyFlag;
import com.umudugudu.entity.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyFlagRepository extends JpaRepository<PenaltyFlag, UUID> {

    List<PenaltyFlag> findByActivityId(UUID activityId);
    List<PenaltyFlag> findByActivityIdAndStatus(UUID activityId, PenaltyStatus status);
    List<PenaltyFlag> findByCitizenId(UUID citizenId);
    Optional<PenaltyFlag> findByActivityIdAndCitizenId(UUID activityId, UUID citizenId);

    List<PenaltyFlag> findByCitizenIdAndStatus(UUID citizenId, PenaltyStatus status);

    @Query("SELECT p FROM PenaltyFlag p WHERE p.citizen.isibo.id = :isiboId")
    List<PenaltyFlag> findByCitizenIsiboId(@Param("isiboId") UUID isiboId);

    @Query("SELECT p FROM PenaltyFlag p WHERE p.citizen.isibo.id = :isiboId AND p.status = :status")
    List<PenaltyFlag> findByCitizenIsiboIdAndStatus(
            @Param("isiboId") UUID isiboId,
            @Param("status") PenaltyStatus status);
}