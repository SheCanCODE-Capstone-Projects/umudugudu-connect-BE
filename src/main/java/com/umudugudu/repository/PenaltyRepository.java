package com.umudugudu.repository;

import com.umudugudu.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, UUID> {
    boolean existsByAttendance_Id(UUID attendanceId);
}
