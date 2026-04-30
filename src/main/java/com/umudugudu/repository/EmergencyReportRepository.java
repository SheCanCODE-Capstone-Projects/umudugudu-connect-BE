package com.umudugudu.repository;

import com.umudugudu.model.EmergencyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmergencyReportRepository extends JpaRepository<EmergencyReport, UUID> {
    Page<EmergencyReport> findByVillageId(UUID villageId, Pageable pageable);
}
