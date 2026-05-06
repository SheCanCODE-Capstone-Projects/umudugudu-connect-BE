package com.umudugudu.dto.request;
import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String email;
    private String phoneNumber;
    private String otp;
}