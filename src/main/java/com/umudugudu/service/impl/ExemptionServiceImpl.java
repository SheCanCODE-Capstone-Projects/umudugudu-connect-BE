package com.umudugudu.service.impl;

import com.umudugudu.dto.request.ExemptionRequest;
import com.umudugudu.dto.response.ExemptionResponse;
import com.umudugudu.entity.Attendance;
import com.umudugudu.entity.AttendanceStatus;
import com.umudugudu.entity.Exemption;
import com.umudugudu.repository.AttendanceRepository;
import com.umudugudu.repository.ExemptionRepository;
import com.umudugudu.service.ExemptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExemptionServiceImpl implements ExemptionService {
    private final AttendanceRepository attendanceRepository;
    private final ExemptionRepository exemptionRepository;

    @Override
    public ExemptionResponse createExemption(ExemptionRequest request) {
        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                .orElseThrow(() -> new RuntimeException("Attendance not found"));


        attendance.setStatus(AttendanceStatus.EXCUSED);

        attendanceRepository.save(attendance);

        Exemption exemption = new Exemption();
        exemption.setAttendance(attendance);
        exemption.setReason(request.getReason());

        exemptionRepository.save(exemption);

        return new ExemptionResponse(
                exemption.getId(),
                attendance.getId(),
                exemption.getReason(),
                "EXCUSED"
        );
    }
}
