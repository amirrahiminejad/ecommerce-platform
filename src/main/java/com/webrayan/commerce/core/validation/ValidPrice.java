package com.webrayan.commerce.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation برای بررسی قیمت معتبر
 */
@Documented
@Constraint(validatedBy = ValidPriceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    
    String message() default "قیمت نامعتبر است";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * حداقل قیمت مجاز
     */
    double min() default 0.0;
    
    /**
     * حداکثر قیمت مجاز
     */
    double max() default Double.MAX_VALUE;
    
    /**
     * آیا قیمت صفر مجاز است یا نه
     */
    boolean allowZero() default false;
}
