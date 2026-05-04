package com.umudugudu.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.umudugudu.dto.request.SmsCommandRequest;
import com.umudugudu.dto.response.SmsResponse;
import com.umudugudu.entity.SmsLog;
import com.umudugudu.entity.User;
import com.umudugudu.repository.SmsLogRepository;
import com.umudugudu.repository.UserRepository;
import com.umudugudu.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsLogRepository smsLogRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.africas-talking.api-key}")
    private String atApiKey;

    @Value("${app.africas-talking.username}")
    private String atUsername;

    @Value("${app.africas-talking.sender-id}")
    private String atSenderId;

    private final Gson gson = new Gson();

    @Override
    public boolean sendSms(String phoneNumber, String message) {
        try {

            return sendSmsViaAfricasTalking(phoneNumber, message);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());

            logSmsFailure(phoneNumber, message, "SMS_API_ERROR", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendOtpSms(String phoneNumber, String otp) {
        String message = "Your Umudugudu Connect OTP is: " + otp + ". Valid for 5 minutes.";
        return sendSms(phoneNumber, message);
    }

    @Override
    public boolean notifyActivityViaSms(UUID userId, String activityMessage) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getPhoneNumber() == null) {
            log.warn("User {} has no phone number for SMS notification", userId);
            return false;
        }

        User user = userOpt.get();
        if (!user.isSmsNotificationsEnabled()) {
            log.debug("SMS notifications disabled for user {}", userId);
            return false;
        }

        return sendSms(user.getPhoneNumber(), activityMessage);
    }

    @Override
    public SmsResponse processSmsCommand(SmsCommandRequest request) {
        try {
            String command = request.getCommand().toUpperCase();
            String phoneNumber = request.getPhoneNumber();

            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                return SmsResponse.builder()
                        .success(false)
                        .message("User not found. Please register first.")
                        .fullResponse("ERROR: User not registered")
                        .build();
            }

            User user = userOpt.get();

            return switch (command) {
                case "STATUS" -> getActivityStatus(user);
                case "PENALTIES" -> getPenalties(user);
                case "ACTIVITIES" -> getActivities(user);
                case "HELP" -> getHelp();
                default -> getHelp();
            };
        } catch (Exception e) {
            log.error("Error processing SMS command: {}", e.getMessage());
            return SmsResponse.builder()
                    .success(false)
                    .message("Error processing command")
                    .fullResponse("ERROR: Unable to process your request")
                    .build();
        }
    }

    @Override
    public boolean retrySms(UUID smsLogId) {
        Optional<SmsLog> logOpt = smsLogRepository.findById(smsLogId);
        if (logOpt.isEmpty()) {
            return false;
        }

        SmsLog log = logOpt.get();
        boolean success = sendSms(log.getPhoneNumber(), log.getMessage());

        if (success) {
            log.setStatus(SmsLog.SmsStatus.SENT);
            smsLogRepository.save(log);
        }

        return success;
    }

    /**
     * Private helper methods
     */

    private boolean sendSmsViaAfricasTalking(String phoneNumber, String message) {
        try {
            String url = "https://api.sandbox.africastalking.com/version1/messaging";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.set("ApiKey", atApiKey);

            String body = "username=" + atUsername
                    + "&to=" + phoneNumber
                    + "&message=" + message
                    + "&from=" + atSenderId;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            var response = restTemplate.postForObject(url, entity, String.class);
            log.info("SMS sent successfully to {}", phoneNumber);

            logSmsSuccess(phoneNumber, message, "AfricasTalking");
            return true;
        } catch (Exception e) {
            log.error("Africa's Talking SMS failed: {}", e.getMessage());
            return false;
        }
    }

    private SmsResponse getActivityStatus(User user) {
        // TODO: Fetch from ActivityService
        List<String> activities = new ArrayList<>();
        activities.add("1. Village cleanup - Saturday 10 AM");
        activities.add("2. Community health screening - Sunday 2 PM");

        String response = "Pending Activities:\n" +
                "1. Village cleanup - Saturday 10 AM\n" +
                "2. Community health screening - Sunday 2 PM\n" +
                "Reply PENALTIES for penalty info.";

        return SmsResponse.builder()
                .success(true)
                .message("Activities retrieved")
                .pendingActivities(activities)
                .fullResponse(response)
                .build();
    }

    private SmsResponse getPenalties(User user) {
        // TODO: Fetch from PenaltyService
        List<String> penalties = new ArrayList<>();
        penalties.add("Missed activity: Garbage collection (500 RWF)");

        String response = "Your Penalties:\n" +
                "1. Missed activity: Garbage collection (500 RWF)\n" +
                "Total outstanding: 500 RWF\n" +
                "Reply STATUS for pending activities.";

        return SmsResponse.builder()
                .success(true)
                .message("Penalties retrieved")
                .penalties(penalties)
                .fullResponse(response)
                .build();
    }

    private SmsResponse getActivities(User user) {
        // TODO: Fetch from ActivityService
        List<String> activities = new ArrayList<>();
        activities.add("Village cleanup - Saturday");
        activities.add("Health screening - Sunday");

        String response = "Your Activities:\n" +
                "1. Village cleanup - Saturday 10 AM\n" +
                "2. Health screening - Sunday 2 PM\n" +
                "Reply STATUS for complete info.";

        return SmsResponse.builder()
                .success(true)
                .message("Activities retrieved")
                .pendingActivities(activities)
                .fullResponse(response)
                .build();
    }

    private SmsResponse getHelp() {
        String helpText = "Umudugudu Connect - SMS Commands:\n" +
                "STATUS - View pending activities & penalties\n" +
                "PENALTIES - View outstanding penalties\n" +
                "ACTIVITIES - View all activities\n" +
                "HELP - Show this message";

        return SmsResponse.builder()
                .success(true)
                .message("Help retrieved")
                .fullResponse(helpText)
                .build();
    }

    private void logSmsSuccess(String phoneNumber, String message, String provider) {
        SmsLog log = new SmsLog();
        log.setPhoneNumber(phoneNumber);
        log.setMessage(message);
        log.setProvider(provider);
        log.setStatus(SmsLog.SmsStatus.SENT);
        smsLogRepository.save(log);
    }

    private void logSmsFailure(String phoneNumber, String message, String provider, String errorMessage) {
        SmsLog log = new SmsLog();
        log.setPhoneNumber(phoneNumber);
        log.setMessage(message);
        log.setProvider(provider);
        log.setStatus(SmsLog.SmsStatus.FAILED);
        log.setErrorMessage(errorMessage);
        smsLogRepository.save(log);
    }
}

