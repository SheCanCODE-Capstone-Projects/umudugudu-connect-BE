package com.umudugudu.service;

import com.umudugudu.dto.request.ExemptionRequest;
import com.umudugudu.dto.response.ExemptionResponse;

public interface ExemptionService {
    ExemptionResponse createExemption(ExemptionRequest request);
}
