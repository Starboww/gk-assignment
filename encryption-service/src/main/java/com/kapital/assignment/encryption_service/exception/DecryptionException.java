package com.kapital.assignment.encryption_service.exception;

public class DecryptionException extends RuntimeException {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
