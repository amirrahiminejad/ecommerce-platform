package com.webrayan.store.modules.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO برای نمایش آگهی در نتایج جستجو
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "آگهی در نتایج جستجو")
public class AdSearchResult {

    @Schema(description = "شناسه آگهی", example = "1")
    private Long id;

    @Schema(description = "عنوان آگهی", example = "فروش موبایل سامسونگ گلکسی")
    private String title;

    @Schema(description = "خلاصه توضیحات", example = "موبایل در حالت عالی...")
    private String shortDescription;

    @Schema(description = "قیمت", example = "5000000")
    private BigDecimal price;

    @Schema(description = "تصویر اصلی", example = "/uploads/images/ad1_main.jpg")
    private String mainImage;

    @Schema(description = "وضعیت", example = "APPROVED")
    private String status;

    @Schema(description = "دسته‌بندی", example = "موبایل و تبلت")
    private String categoryName;

    @Schema(description = "شناسه دسته‌بندی", example = "1")
    private Long categoryId;

    @Schema(description = "موقعیت مکانی", example = "تهران")
    private String locationName;

    @Schema(description = "شناسه موقعیت", example = "1")
    private Long locationId;

    @Schema(description = "نام فروشنده", example = "احمد رضایی")
    private String sellerName;

    @Schema(description = "شناسه فروشنده", example = "1")
    private Long sellerId;

    @Schema(description = "تعداد بازدید", example = "125")
    private Integer viewsCount;

    @Schema(description = "آیا ویژه است", example = "false")
    private Boolean isFeatured;

    @Schema(description = "آیا فعال است", example = "true")
    private Boolean isActive;

    @Schema(description = "تاریخ ایجاد")
    private LocalDateTime createdAt;

    @Schema(description = "تاریخ آخرین بروزرسانی")
    private LocalDateTime updatedAt;

    @Schema(description = "برچسب‌ها", example = "[\"نو\", \"گارانتی\"]")
    private List<String> tags;

    @Schema(description = "تصاویر اضافی")
    private List<String> additionalImages;

    @Schema(description = "امتیاز ارتباط (relevance score)", example = "0.85")
    private Double relevanceScore;

    @Schema(description = "فاصله از موقعیت جستجو (کیلومتر)", example = "5.2")
    private Double distanceKm;

    @Schema(description = "ویژگی‌های اضافی")
    private List<AdAttributeDto> attributes;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdAttributeDto {
        private String name;
        private String value;
        private String type;
    }

    // Helper methods
    public String getFormattedPrice() {
        if (price == null) return "توافقی";
        return String.format("%,d تومان", price.intValue());
    }

    public String getTimeAgo() {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(createdAt, now).toDays();
        
        if (days == 0) return "امروز";
        if (days == 1) return "دیروز";
        if (days < 7) return days + " روز پیش";
        if (days < 30) return (days / 7) + " هفته پیش";
        return (days / 30) + " ماه پیش";
    }

    public boolean hasMainImage() {
        return mainImage != null && !mainImage.trim().isEmpty();
    }

    public boolean hasAdditionalImages() {
        return additionalImages != null && !additionalImages.isEmpty();
    }

    public int getTotalImagesCount() {
        int count = hasMainImage() ? 1 : 0;
        count += hasAdditionalImages() ? additionalImages.size() : 0;
        return count;
    }
}
