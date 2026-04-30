package com.umudugudu.repository;

import com.umudugudu.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByPerformedById(UUID userId, Pageable pageable);
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
}
