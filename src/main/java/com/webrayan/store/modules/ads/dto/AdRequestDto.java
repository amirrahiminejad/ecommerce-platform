package com.webrayan.store.modules.ads.dto;

import com.webrayan.store.core.validation.ValidPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "درخواست ایجاد یا ویرایش آگهی")
public class AdRequestDto {
    
    @Schema(description = "شناسه دسته‌بندی", example = "1", required = true)
    @NotNull(message = "شناسه دسته‌بندی نمی‌تواند خالی باشد")
    @Positive(message = "شناسه دسته‌بندی باید عدد مثبت باشد")
    private Long categoryId;
    
    @Schema(description = "عنوان آگهی", example = "فروش خودرو پراید", required = true)
    @NotBlank(message = "عنوان آگهی نمی‌تواند خالی باشد")
    @Size(min = 5, max = 200, message = "عنوان آگهی باید بین 5 تا 200 کاراکتر باشد")
    private String title;
    
    @Schema(description = "توضیحات آگهی", example = "خودرو در حالت عالی، کم کارکرد")
    @Size(max = 2000, message = "توضیحات نمی‌تواند بیش از 2000 کاراکتر باشد")
    private String description;
    
    @Schema(description = "قیمت (به تومان)", example = "50000000")
    @ValidPrice(min = 1000, max = 999999999999.99, message = "قیمت باید بین 1000 تا 999 میلیارد تومان باشد")
    private Double price;
    
    @Schema(description = "شناسه موقعیت", example = "1", required = true)
    @NotNull(message = "شناسه موقعیت نمی‌تواند خالی باشد")
    @Positive(message = "شناسه موقعیت باید عدد مثبت باشد")
    private Long locationId;
    
    @Schema(description = "قابل مذاکره بودن", example = "true")
    private Boolean negotiable = false;
}
