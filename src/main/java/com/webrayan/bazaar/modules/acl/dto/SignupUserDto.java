package com.webrayan.bazaar.modules.acl.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "اطلاعات ثبت نام کاربر جدید")
public class SignupUserDto {
    
    @Schema(
        description = "آدرس ایمیل", 
        example = "user@example.com",
        required = true
    )
    @NotBlank(message = "آدرس ایمیل نمی‌تواند خالی باشد")
    @Email(message = "فرمت آدرس ایمیل صحیح نیست")
    private String email;
    
    @Schema(
        description = "رمز عبور", 
        example = "password123",
        required = true,
        minLength = 6
    )
    @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
    @Size(min = 6, max = 100, message = "رمز عبور باید بین 6 تا 100 کاراکتر باشد")
    private String password;
    
    @Schema(
        description = "نام", 
        example = "احمد",
        required = true
    )
    @NotBlank(message = "نام نمی‌تواند خالی باشد")
    @Size(min = 2, max = 30, message = "نام باید بین 2 تا 30 کاراکتر باشد")
    private String firstName;
    
    @Schema(
        description = "نام خانوادگی", 
        example = "احمدی",
        required = true
    )
    @NotBlank(message = "نام خانوادگی نمی‌تواند خالی باشد")
    @Size(min = 2, max = 30, message = "نام خانوادگی باید بین 2 تا 30 کاراکتر باشد")
    private String lastName;
    
    @Schema(
        description = "نام کاربری", 
        example = "ahmad123",
        required = true
    )
    @NotBlank(message = "نام کاربری نمی‌تواند خالی باشد")
    @Size(min = 3, max = 30, message = "نام کاربری باید بین 3 تا 30 کاراکتر باشد")
    private String username;
    
    @Schema(
        description = "شماره تماس", 
        example = "09123456789",
        required = false
    )
    private String phone;
}
