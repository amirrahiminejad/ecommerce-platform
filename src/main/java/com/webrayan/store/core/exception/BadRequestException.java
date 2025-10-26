package com.webrayan.store.core.exception;

/**
 * خطای درخواست نامعتبر
 */
public class BadRequestException extends RuntimeException {
    
    private final String reason;

    public BadRequestException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public BadRequestException(String message) {
        super(message);
        this.reason = null;
    }

    public String getReason() {
        return reason;
    }
}
