package com.umudugudu.exception;

public class OtpInvalidException extends BusinessException {
    public OtpInvalidException(int attemptsRemaining) {
        super("Invalid code — " + attemptsRemaining + " attempt" +
              (attemptsRemaining == 1 ? "" : "s") + " remaining");
    }
}
