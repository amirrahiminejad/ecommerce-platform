package com.webrayan.store.core.exception;

/**
 * خطای عدم مجوز برای انجام عملیات
 */
public class UnauthorizedOperationException extends RuntimeException {
    
    private final String operation;
    private final String resource;

    public UnauthorizedOperationException(String operation, String resource) {
        super(String.format("شما مجوز انجام عملیات '%s' روی منبع '%s' را ندارید", operation, resource));
        this.operation = operation;
        this.resource = resource;
    }

    public UnauthorizedOperationException(String message) {
        super(message);
        this.operation = null;
        this.resource = null;
    }

    public String getOperation() {
        return operation;
    }

    public String getResource() {
        return resource;
    }
}
