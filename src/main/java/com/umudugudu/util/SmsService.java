package com.umudugudu.util;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendSms(String phone, String message) {
        System.out.println("SMS to " + phone + ": " + message);
    }
}