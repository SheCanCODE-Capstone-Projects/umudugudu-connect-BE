package com.umudugudu.repository;

import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Fixed: Isibo.id is Long not UUID
    List<Attendance> findByCitizen_Isibo_Id(UUID isiboId);

    // All attendance records for one activity that belong to a specific isibo
    @Query("SELECT a FROM Attendance a WHERE a.activityId = :activityId AND a.citizen.isibo.id = :isiboId")
    List<Attendance> findByActivityIdAndIsiboId(
            @Param("activityId") UUID activityId,
            @Param("isiboId") UUID isiboId);

    // Count by activity and status — used for fast totals
    UUID countByActivityIdAndStatus(UUID activityId, AttendanceStatus status);

    // Total records for an activity — used as totalInvited
    UUID countByActivityId(UUID activityId);
}