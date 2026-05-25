package com.umudugudu.controller;

import com.umudugudu.dto.request.ExemptionRequest;
import com.umudugudu.dto.response.ExemptionResponse;
import com.umudugudu.service.ExemptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exemptions")
@RequiredArgsConstructor
public class ExemptionController {
    private final ExemptionService exemptionService;

    @PostMapping
    public ExemptionResponse create(@RequestBody ExemptionRequest request) {
        return exemptionService.createExemption(request);
    }
}
