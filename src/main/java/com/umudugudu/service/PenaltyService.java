package com.umudugudu.service;

import com.umudugudu.dto.request.AssignPenaltyRequest;
import com.umudugudu.dto.response.PenaltyResponse;
import com.umudugudu.entity.Attendance;

public interface PenaltyService  {
    void generatePenaltyIfNeeded(Attendance attendance);

    PenaltyResponse assignPenalty(
            AssignPenaltyRequest request
    );
}
