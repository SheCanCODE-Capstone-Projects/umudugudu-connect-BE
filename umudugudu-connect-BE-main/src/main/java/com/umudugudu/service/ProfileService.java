package com.umudugudu.service;

import com.umudugudu.dto.request.ProfileChangeRequestDTO;
import com.umudugudu.dto.request.ReviewChangeRequestDTO;
import com.umudugudu.dto.response.ChangeRequestResponse;
import com.umudugudu.dto.response.ProfileResponse;
import java.util.List;

public interface ProfileService {
    ProfileResponse getMyProfile(String username);
    ChangeRequestResponse submitChangeRequest(String username, ProfileChangeRequestDTO dto);
    List<ChangeRequestResponse> getMyChangeRequests(String username);
    List<ChangeRequestResponse> getPendingChangeRequests(String leaderUsername);
    ChangeRequestResponse reviewChangeRequest(String leaderUsername, Long requestId, ReviewChangeRequestDTO dto);
}