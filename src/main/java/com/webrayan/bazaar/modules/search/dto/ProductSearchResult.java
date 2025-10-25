package com.webrayan.bazaar.modules.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO برای نمایش محصول در نتایج جستجو
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "محصول در نتایج جستجو")
public class ProductSearchResult {

    @Schema(description = "شناسه محصول", example = "1")
    private Long id;

    @Schema(description = "نام محصول", example = "گوشی سامسونگ گلکسی A54")
    private String name;

    @Schema(description = "توضیح کوتاه", example = "گوشی هوشمند با دوربین 64 مگاپیکسل")
    private String shortDescription;

    @Schema(description = "قیمت", example = "8500000")
    private BigDecimal price;

    @Schema(description = "قیمت تخفیف‌دار", example = "7650000")
    private BigDecimal discountPrice;

    @Schema(description = "درصد تخفیف", example = "10")
    private Integer discountPercentage;

    @Schema(description = "تصویر اصلی", example = "/uploads/products/samsung_a54_main.jpg")
    private String mainImage;

    @Schema(description = "وضعیت", example = "PUBLISHED")
    private String status;

    @Schema(description = "دسته‌بندی", example = "گوشی موبایل")
    private String categoryName;

    @Schema(description = "شناسه دسته‌بندی", example = "1")
    private Long categoryId;

    @Schema(description = "نام فروشنده", example = "فروشگاه تکنولوژی")
    private String sellerName;

    @Schema(description = "شناسه فروشنده", example = "1")
    private Long sellerId;

    @Schema(description = "موجودی", example = "15")
    private Integer stockQuantity;

    @Schema(description = "کد محصول (SKU)", example = "SAM-A54-128GB")
    private String sku;

    @Schema(description = "تعداد بازدید", example = "1250")
    private Long viewsCount;

    @Schema(description = "تعداد فروش", example = "85")
    private Long salesCount;

    @Schema(description = "آیا ویژه است", example = "true")
    private Boolean isFeatured;

    @Schema(description = "آیا فعال است", example = "true")
    private Boolean isActive;

    @Schema(description = "آیا دیجیتال است", example = "false")
    private Boolean isDigital;

    @Schema(description = "تاریخ ایجاد")
    private LocalDateTime createdAt;

    @Schema(description = "تاریخ آخرین بروزرسانی")
    private LocalDateTime updatedAt;

    @Schema(description = "برچسب‌ها", example = "[\"جدید\", \"پرفروش\"]")
    private List<String> tags;

    @Schema(description = "تصاویر اضافی")
    private List<String> additionalImages;

    @Schema(description = "امتیاز ارتباط (relevance score)", example = "0.92")
    private Double relevanceScore;

    @Schema(description = "میانگین امتیاز", example = "4.2")
    private Double averageRating;

    @Schema(description = "تعداد نظرات", example = "23")
    private Integer reviewsCount;

    @Schema(description = "ویژگی‌های محصول")
    private List<ProductAttributeDto> attributes;

    @Schema(description = "ابعاد و وزن")
    private ProductDimensionsDto dimensions;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductAttributeDto {
        private String name;
        private String value;
        private String unit;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDimensionsDto {
        private BigDecimal weight;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
    }

    // Helper methods
    public String getFormattedPrice() {
        BigDecimal finalPrice = hasDiscount() ? discountPrice : price;
        if (finalPrice == null) return "تماس بگیرید";
        return String.format("%,d تومان", finalPrice.intValue());
    }

    public String getFormattedOriginalPrice() {
        if (price == null) return "";
        return String.format("%,d تومان", price.intValue());
    }

    public boolean hasDiscount() {
        return discountPrice != null && price != null && discountPrice.compareTo(price) < 0;
    }

    public Integer getCalculatedDiscountPercentage() {
        if (!hasDiscount()) return 0;
        
        BigDecimal discount = price.subtract(discountPrice);
        BigDecimal percentage = discount.divide(price, 4, BigDecimal.ROUND_HALF_UP)
                                      .multiply(BigDecimal.valueOf(100));
        return percentage.intValue();
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity > 0 && stockQuantity <= 5;
    }

    public String getStockStatus() {
        if (!isInStock()) return "ناموجود";
        if (isLowStock()) return "آخرین موجودی";
        return "موجود";
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

    public String getRatingStars() {
        if (averageRating == null) return "☆☆☆☆☆";
        
        int stars = averageRating.intValue();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 1; i <= 5; i++) {
            if (i <= stars) {
                sb.append("★");
            } else {
                sb.append("☆");
            }
        }
        
        return sb.toString();
    }
}
