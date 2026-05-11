package com.umudugudu.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void sendSms(String phone, String message) {
        // TODO: integrate Africa's Talking after project completion
        log.info("[SMS] To: {} | Message: {}", phone, message);
    }
}
