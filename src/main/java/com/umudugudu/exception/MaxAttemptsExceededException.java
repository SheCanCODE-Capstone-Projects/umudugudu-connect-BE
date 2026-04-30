package com.umudugudu.exception;

public class MaxAttemptsExceededException extends BusinessException {
    public MaxAttemptsExceededException() {
        super("Maximum OTP attempts exceeded. Please request a new code.");
    }
}
