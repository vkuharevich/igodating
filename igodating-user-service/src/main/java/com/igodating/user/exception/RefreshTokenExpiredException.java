package com.igodating.user.exception;

public class RefreshTokenExpiredException extends RuntimeException{
    public RefreshTokenExpiredException() {
    }

    public RefreshTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
