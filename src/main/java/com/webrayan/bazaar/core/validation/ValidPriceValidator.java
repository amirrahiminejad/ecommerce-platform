package com.webrayan.bazaar.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator برای annotation ValidPrice
 */
public class ValidPriceValidator implements ConstraintValidator<ValidPrice, Double> {
    
    private double min;
    private double max;
    private boolean allowZero;
    
    @Override
    public void initialize(ValidPrice constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.allowZero = constraintAnnotation.allowZero();
    }
    
    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        // اگر مقدار null باشد، valid محسوب می‌شود (از @NotNull برای چک کردن null استفاده کنید)
        if (value == null) {
            return true;
        }
        
        // بررسی صفر بودن
        if (value == 0.0 && !allowZero) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("قیمت نمی‌تواند صفر باشد")
                   .addConstraintViolation();
            return false;
        }
        
        // بررسی منفی بودن
        if (value < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("قیمت نمی‌تواند منفی باشد")
                   .addConstraintViolation();
            return false;
        }
        
        // بررسی محدوده
        if (value < min || value > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("قیمت باید بین %.2f تا %.2f تومان باشد", min, max))
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
