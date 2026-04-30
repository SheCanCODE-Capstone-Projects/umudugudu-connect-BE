package com.umudugudu.exception;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid or has expired.");
    }
}
