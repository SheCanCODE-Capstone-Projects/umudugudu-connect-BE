package com.umudugudu.dto.request;
import lombok.Data;

@Data
public class OtpVerifyRequestEmail {
    private String email;
    private String otp;
}