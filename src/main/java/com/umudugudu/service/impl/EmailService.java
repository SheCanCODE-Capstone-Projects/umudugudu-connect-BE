package com.umudugudu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromEmail);
            message.setSubject("Umudugudu Connect – Your Verification Code");
            message.setText(
                    "Dear User,\n\n" +
                            "Thank you for registering with Umudugudu Connect.\n\n" +
                            "Your One-Time Password (OTP) is:\n\n" +
                            "        " + otp + "\n\n" +
                            "This code is valid for 5 minutes. Please do not share it with anyone.\n\n" +
                            "If you did not request this code, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "The Umudugudu Connect Team"
            );

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", to);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Could not send OTP email. Please try again.");
        }
    }
}