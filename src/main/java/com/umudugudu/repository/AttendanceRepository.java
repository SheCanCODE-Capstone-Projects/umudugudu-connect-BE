package com.umudugudu.repository;

import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByActivityId(UUID activityId);
    List<Attendance> findByActivityIdAndStatus(UUID activityId, AttendanceStatus status);
    Optional<Attendance> findByActivityIdAndCitizenId(UUID activityId, Long citizenId);
    List<Attendance> findByCitizenId(Long citizenId);
    List<Attendance> findByCitizen_Id(Long citizenId);
    List<Attendance> findByCitizen_Isibo_Id(Long id);
}