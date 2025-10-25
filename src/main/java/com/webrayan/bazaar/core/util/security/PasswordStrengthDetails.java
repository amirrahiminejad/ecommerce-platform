package com.webrayan.bazaar.core.util.security;

public class PasswordStrengthDetails {

    private boolean _isStrong;
    private String _message;

    public PasswordStrengthDetails(boolean isStrong, String message) {
        _isStrong = isStrong;
        _message = message;
    }

    public boolean isStrong() {
        return _isStrong;
    }

    public String getMessage() {
        return _message;
    }
}
