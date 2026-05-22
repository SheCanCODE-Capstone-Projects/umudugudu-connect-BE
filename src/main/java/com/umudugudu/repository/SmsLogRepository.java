package com.umudugudu.repository;

import com.umudugudu.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, UUID> {
    List<SmsLog> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<SmsLog> findByPhoneNumberAndStatus(String phoneNumber, SmsLog.SmsStatus status);
}

