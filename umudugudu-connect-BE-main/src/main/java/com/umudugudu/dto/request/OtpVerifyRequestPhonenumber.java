package com.umudugudu.dto.request;

import lombok.Data;

@Data
public class OtpVerifyRequestPhonenumber {
        private String phoneNumber;
        private String otp;
}
