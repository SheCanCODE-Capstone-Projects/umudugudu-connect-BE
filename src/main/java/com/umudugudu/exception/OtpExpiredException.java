package com.umudugudu.exception;

public class OtpExpiredException extends BusinessException {
    public OtpExpiredException() {
        super("OTP has expired. Please request a new one.");
    }
}
