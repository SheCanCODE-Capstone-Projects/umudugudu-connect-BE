package com.umudugudu.dto.request;

import lombok.Data;

@Data
public class PhoneLoginRequest {
    private String phoneNumber;
    private String password;
}