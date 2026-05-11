package com.umudugudu.service;

import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.User;

import java.util.List;
import java.util.UUID;

public interface PenaltyFlagService {
    void handleAttendanceStatus(Attendance attendance);
    PenaltyFlagResponse reviewPenalty(Long flagId, ReviewPenaltyRequest request, User villageLeader);
    List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId);

    List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId);
    List<PenaltyFlagResponse> getFlagsForCitizen(Long citizenId);
}