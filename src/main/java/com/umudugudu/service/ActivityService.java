package com.umudugudu.service;

import com.umudugudu.dto.request.ActivityRequest;
import com.umudugudu.dto.response.ActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityService {
    ActivityResponse create(ActivityRequest request, Long creatorId);
    Page<ActivityResponse> list(Pageable pageable);
    ActivityResponse getById(Long id);
    ActivityResponse update(Long id, ActivityRequest request, Long requesterId);
}
