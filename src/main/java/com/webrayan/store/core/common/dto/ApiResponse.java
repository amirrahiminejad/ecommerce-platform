package com.webrayan.store.core.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * کلاس مشترک برای پاسخ‌های API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "پاسخ استاندارد API")
public class ApiResponse<T> {
    
    @Schema(description = "وضعیت موفقیت عملیات", example = "true")
    private boolean success;
    
    @Schema(description = "پیام توضیحی", example = "عملیات با موفقیت انجام شد")
    private String message;
    
    @Schema(description = "داده‌های پاسخ")
    private T data;
    
    @Schema(description = "کد خطا (در صورت وجود)", example = "400")
    private String errorCode;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "عملیات با موفقیت انجام شد", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
}
