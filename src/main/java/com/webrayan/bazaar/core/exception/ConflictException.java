package com.webrayan.bazaar.core.exception;

/**
 * خطای تداخل در عملیات (مثل duplicate key)
 */
public class ConflictException extends RuntimeException {
    
    private final String conflictField;
    private final Object conflictValue;

    public ConflictException(String conflictField, Object conflictValue) {
        super(String.format("تداخل در فیلد '%s' با مقدار '%s'", conflictField, conflictValue));
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
    }

    public ConflictException(String message) {
        super(message);
        this.conflictField = null;
        this.conflictValue = null;
    }

    public String getConflictField() {
        return conflictField;
    }

    public Object getConflictValue() {
        return conflictValue;
    }
}
