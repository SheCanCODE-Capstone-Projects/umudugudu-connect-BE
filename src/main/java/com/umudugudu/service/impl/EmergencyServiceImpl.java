package com.umudugudu.service.impl;

import com.umudugudu.dto.request.CreateEmergencyRequest;
import com.umudugudu.entity.EmergencyReport;
import com.umudugudu.entity.Role;
import com.umudugudu.entity.User;
import com.umudugudu.entity.Village;
import com.umudugudu.repository.EmergencyRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.EmergencyService;
import com.umudugudu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyServiceImpl implements EmergencyService {
    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    @Override
    public void reportEmergency(CreateEmergencyRequest request) {

        log.info(" EMERGENCY FLOW STARTED");
        EmergencyReport report = EmergencyReport.builder()
                .type(request.getType())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .villageId(request.getVillageId())
                .createdAt(LocalDateTime.now())
                .resolved(false)
                .build();

        emergencyRepository.save(report);




        Village village = new Village();
        village.setId(request.getVillageId());

        List<User> leaders =
                userRepository.findByVillageAndRole(
                        village,
                        Role.VILLAGE_LEADER
                );


        log.info("📢 Sending notifications...");
        notificationService.sendToUsers(
                leaders,
                " EMERGENCY ALERT",
                request.getType() + " - " + request.getDescription()
        );

    }


    @Override
    public void broadcastEmergency(String message, UUID villageId) {

        Village village = new Village();
        village.setId(villageId);

        List<User> citizens =
                userRepository.findByVillage(village);

        notificationService.sendToUsers(
                citizens,
                "VILLAGE EMERGENCY",
                message
        );
    }

    @Override
    public List<EmergencyReport> getAllEmergencies() {
        return emergencyRepository.findAll();
    }

    @Override
    public EmergencyReport getEmergencyById(UUID id) {
        return emergencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency not found"));
    }
}

