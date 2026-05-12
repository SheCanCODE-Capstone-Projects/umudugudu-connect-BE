package com.umudugudu.repository;

import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    List<Attendance> findByActivityId(UUID activityId);
    List<Attendance> findByActivityIdAndStatus(UUID activityId, AttendanceStatus status);
    Optional<Attendance> findByActivityIdAndCitizenId(UUID activityId, UUID citizenId);
    List<Attendance> findByCitizenId(UUID citizenId);
    List<Attendance> findByCitizen_Id(UUID citizenId);
    List<Attendance> findByCitizen_Isibo_Id(UUID id);
}