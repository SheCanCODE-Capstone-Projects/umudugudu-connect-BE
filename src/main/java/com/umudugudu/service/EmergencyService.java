package com.umudugudu.service;

import com.umudugudu.dto.request.CreateEmergencyRequest;
import com.umudugudu.entity.EmergencyReport;

import java.util.List;
import java.util.UUID;

public interface EmergencyService {
    void reportEmergency(CreateEmergencyRequest request);
    void broadcastEmergency(String message, UUID villageId);
    List<EmergencyReport> getAllEmergencies();
    EmergencyReport getEmergencyById(UUID id);
}
