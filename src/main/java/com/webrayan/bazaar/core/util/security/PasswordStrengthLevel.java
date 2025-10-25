package com.webrayan.bazaar.core.util.security;

public enum PasswordStrengthLevel {

    WEAK(1),
    MEDIUM(2),
    STRONG(3),
    VERY_STRONG(4),
    EXTREMELY_STRONG(5);

    private final int level;

    PasswordStrengthLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
