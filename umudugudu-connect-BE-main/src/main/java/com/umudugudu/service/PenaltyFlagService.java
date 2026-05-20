package com.umudugudu.service;

import com.umudugudu.dto.request.AssignPenaltyRequest;
import com.umudugudu.dto.request.ExemptPenaltyRequest;
import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.IsiboPenaltySummaryResponse;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.dto.response.PenaltyResponse;
import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.User;

import java.util.List;
import java.util.UUID;

public interface PenaltyFlagService {

    void handleAttendanceStatus(Attendance attendance);
    PenaltyFlagResponse reviewPenalty(UUID flagId, ReviewPenaltyRequest request, User villageLeader);
    List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId);
    List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId);
    List<PenaltyFlagResponse> getFlagsForCitizen(UUID citizenId);

    PenaltyResponse assignPenalty(UUID flagId, AssignPenaltyRequest request, User villageLeader);
    PenaltyResponse exemptCitizen(UUID flagId, ExemptPenaltyRequest request, User villageLeader);

    List<PenaltyResponse> getMyPenalties(User citizen);

    IsiboPenaltySummaryResponse getIsiboPenaltySummary(User isiboLeader);
}