package com.webrayan.commerce.modules.acl.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "اطلاعات ورود کاربر")
public class LoginUserDto {
    
    @Schema(
        description = "نام کاربری یا آدرس ایمیل", 
        example = "admin",
        required = true
    )
    @NotBlank(message = "نام کاربری نمی‌تواند خالی باشد")
    @Size(min = 3, max = 50, message = "نام کاربری باید بین 3 تا 50 کاراکتر باشد")
    private String username;
    
    @Schema(
        description = "رمز عبور", 
        example = "123",
        required = true
    )
    @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
    @Size(min = 3, max = 100, message = "رمز عبور باید بین 3 تا 100 کاراکتر باشد")
    private String password;
}
