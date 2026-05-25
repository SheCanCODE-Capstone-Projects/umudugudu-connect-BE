package com.umudugudu.service;

import com.umudugudu.dto.request.ReviewPenaltyRequest;
import com.umudugudu.dto.response.IsiboHouseholdPenaltyOverview;
import com.umudugudu.dto.response.PenaltyFlagResponse;
import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.User;
<<<<<<< HEAD
=======
import org.springframework.stereotype.Service;
>>>>>>> be3e460 (E3.1 assign penalty to absent citizen)

import java.util.List;
import java.util.UUID;

public interface PenaltyFlagService {
    void handleAttendanceStatus(Attendance attendance);
    PenaltyFlagResponse reviewPenalty(UUID flagId, ReviewPenaltyRequest request, User villageLeader);
    List<PenaltyFlagResponse> getFlagsForActivity(UUID activityId);

    List<PenaltyFlagResponse> getPendingFlagsForActivity(UUID activityId);
    List<PenaltyFlagResponse> getFlagsForCitizen(UUID citizenId);

    // Get household penalties overview for isibo leader
    IsiboHouseholdPenaltyOverview getHouseholdPenaltiesByIsibo(UUID isiboId);
}