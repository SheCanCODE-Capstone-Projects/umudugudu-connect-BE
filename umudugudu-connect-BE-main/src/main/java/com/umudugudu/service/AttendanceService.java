package com.umudugudu.service;

import com.umudugudu.dto.request.BulkAttendanceSyncRequest;
import com.umudugudu.dto.request.MarkAttendanceRequest;
import com.umudugudu.dto.response.AttendanceResponse;
import com.umudugudu.entity.User;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {
    AttendanceResponse markAttendance(UUID activityId, MarkAttendanceRequest request, User isiboLeader);
    List<AttendanceResponse> syncOfflineAttendance(BulkAttendanceSyncRequest request, User isiboLeader);
    List<AttendanceResponse> getAttendanceForActivity(UUID activityId);
    List<AttendanceResponse> getAbsentMembers(UUID activityId);
    List<AttendanceResponse> getAttendanceForIsibo(UUID isiboId);

    List<AttendanceResponse> getAttendanceForCitizen(UUID citizenId);
}