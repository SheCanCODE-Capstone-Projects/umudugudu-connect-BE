package com.umudugudu.dto.request;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phoneNumber;
    private String code;
}