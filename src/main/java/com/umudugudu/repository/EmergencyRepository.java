package com.umudugudu.repository;

import com.umudugudu.entity.EmergencyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmergencyRepository extends JpaRepository<EmergencyReport, UUID> {
}
