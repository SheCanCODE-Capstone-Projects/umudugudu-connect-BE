package com.umudugudu.service;

import com.umudugudu.dto.request.ReviewServiceRequestDto;
import com.umudugudu.dto.request.SubmitServiceRequestDto;
import com.umudugudu.dto.response.ServiceRequestResponse;
import com.umudugudu.entity.User;

import java.util.List;
import java.util.UUID;

public interface ServiceRequestService {

    // US-5.1 — Citizen submits a request
    ServiceRequestResponse submit(SubmitServiceRequestDto dto, User citizen);

    // US-5.2 — Leader reviews (approve / reject / info_required)
    ServiceRequestResponse review(UUID requestId, ReviewServiceRequestDto dto, User leader);

    // US-5.3 — Citizen tracks own requests
    List<ServiceRequestResponse> getMyRequests(User citizen);

    // Leader / Admin: pending queue for their scope
    List<ServiceRequestResponse> getPendingQueue(User leader);

    // Leader / Admin: full history for their scope
    List<ServiceRequestResponse> getFullQueue(User leader);
}
