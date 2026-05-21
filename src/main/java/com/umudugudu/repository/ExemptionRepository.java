package com.umudugudu.repository;

import com.umudugudu.entity.Exemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExemptionRepository extends JpaRepository<Exemption, UUID> {
    Optional<Exemption> findByCitizen_IdAndActivityIdAndApprovedTrue(
            UUID citizenId,
            UUID activityId
    );
}
