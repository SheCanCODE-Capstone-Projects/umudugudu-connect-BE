package com.umudugudu.repository;

import com.umudugudu.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {
    List<AttendanceRecord> findByActivityId(UUID activityId);
    List<AttendanceRecord> findByCitizenId(UUID citizenId);
}
