package com.webrayan.store.core.util.security;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public class PasswordUtil {

    public static boolean isPasswordStrong(String password, PasswordStrengthLevel strengthLevel) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLowerCase = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();

        switch (strengthLevel) {
            case WEAK:
                return true;
            case MEDIUM:
                return (hasUpperCase || hasLowerCase) && (hasDigit || hasSpecialChar);
            case STRONG:
                return hasUpperCase && hasLowerCase;
            case VERY_STRONG:
                return hasUpperCase && hasLowerCase && hasDigit;
            case EXTREMELY_STRONG:
                return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
            default:
                throw new IllegalArgumentException("میزان قدرت باید بین 1 تا 5 باشد.");
        }
    }

    public static String generateRandomPassword(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_=+<>?";
        String allChars = upperCase + lowerCase + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allChars.length());
            password.append(allChars.charAt(index));
        }

        return password.toString();
    }

    public static boolean isCommonPassword(String password) {
        String[] commonPasswords = {
                "123456", "password", "123456789", "12345678", "12345", "1234567", "qwerty", "abc123", "letmein", "monkey"
                // می‌توانید پسوردهای رایج بیشتری اضافه کنید
        };

        for (String common : commonPasswords) {
            if (common.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static PasswordStrengthDetails getPasswordStrengthDetails(String password, PasswordStrengthLevel strengthLevel) {
        if (password.length() < 8) {
            return new PasswordStrengthDetails(false, "پسورد باید حداقل 8 کاراکتر باشد.");
        }

        boolean hasUpperCase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLowerCase = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();

        switch (strengthLevel) {
            case WEAK:
                return new PasswordStrengthDetails(true, "پسورد ضعیف است، اما معتبر است.");
            case MEDIUM:
                if ((hasUpperCase || hasLowerCase) && (hasDigit || hasSpecialChar)) {
                    return new PasswordStrengthDetails(true, "پسورد متوسط است.");
                } else {
                    return new PasswordStrengthDetails(false, "پسورد باید شامل حروف بزرگ یا کوچک و اعداد یا کاراکترهای خاص باشد.");
                }
            case STRONG:
                if (hasUpperCase && hasLowerCase) {
                    return new PasswordStrengthDetails(true, "پسورد قوی است.");
                } else {
                    return new PasswordStrengthDetails(false, "پسورد باید شامل حروف بزرگ و کوچک باشد.");
                }
            case VERY_STRONG:
                if (hasUpperCase && hasLowerCase && hasDigit) {
                    return new PasswordStrengthDetails(true, "پسورد خیلی قوی است.");
                } else {
                    return new PasswordStrengthDetails(false, "پسورد باید شامل حروف بزرگ، کوچک و اعداد باشد.");
                }
            case EXTREMELY_STRONG:
                if (hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar) {
                    return new PasswordStrengthDetails(true, "پسورد فوق‌العاده قوی است.");
                } else {
                    return new PasswordStrengthDetails(false, "پسورد باید شامل حروف بزرگ، کوچک، اعداد و کاراکترهای خاص باشد.");
                }
            default:
                throw new IllegalArgumentException("میزان قدرت باید بین 1 تا 5 باشد.");
        }
    }

}
