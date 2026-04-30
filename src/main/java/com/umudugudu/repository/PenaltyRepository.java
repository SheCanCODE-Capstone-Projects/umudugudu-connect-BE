package com.umudugudu.repository;

import com.umudugudu.model.Penalty;
import com.umudugudu.model.PenaltyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PenaltyRepository extends JpaRepository<Penalty, UUID> {
    List<Penalty> findByCitizenId(UUID citizenId);
    List<Penalty> findByCitizenIdAndStatus(UUID citizenId, PenaltyStatus status);
    Page<Penalty> findByActivityVillageId(UUID villageId, Pageable pageable);
    List<Penalty> findByCitizenIsibId(UUID isibId);
}
