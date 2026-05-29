package com.umudugudu.service;

import com.umudugudu.dto.request.SmsCommandRequest;
import com.umudugudu.dto.response.SmsResponse;

import java.util.UUID;

public interface SmsService {
    /**
     * Send SMS to a phone number
     */
    boolean sendSms(String phoneNumber, String message);

    /**
     * Send OTP via SMS
     */
    boolean sendOtpSms(String phoneNumber, String otp);

    /**
     * Notify user about activity via SMS
     */
    boolean notifyActivityViaSms(UUID userId, String activityMessage);

    /**
     * Process incoming SMS commands
     */
    SmsResponse processSmsCommand(SmsCommandRequest request);

    /**
     * Retry failed SMS
     */
    boolean retrySms(UUID smsLogId);
}

