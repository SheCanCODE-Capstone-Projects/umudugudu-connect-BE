
package com.umudugudu.service;

import com.umudugudu.dto.response.ActivityPerformanceResponse;
import com.umudugudu.entity.User;

import java.util.List;
import java.util.UUID;

public interface ActivityPerformanceService {

    /** Summary + isibo breakdown for one activity */
    ActivityPerformanceResponse getActivityPerformance(UUID activityId, User villageLeader);

    /** All activities for the village leader's village */
    List<ActivityPerformanceResponse> getAllActivityPerformance(User villageLeader);
}