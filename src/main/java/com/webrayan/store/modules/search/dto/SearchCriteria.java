package com.webrayan.store.modules.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.List;

/**
 * جستجو criteria برای محصولات و آگهی‌ها
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "معیارهای جستجو")
public class SearchCriteria {

    @Schema(description = "کلمه کلیدی جستجو", example = "موبایل سامسونگ")
    private String keyword;

    @Schema(description = "شناسه دسته‌بندی", example = "1")
    private Long categoryId;

    @Schema(description = "حداقل قیمت", example = "100000")
    @Min(value = 0, message = "حداقل قیمت نمی‌تواند منفی باشد")
    private BigDecimal minPrice;

    @Schema(description = "حداکثر قیمت", example = "10000000")
    @Min(value = 0, message = "حداکثر قیمت نمی‌تواند منفی باشد")
    private BigDecimal maxPrice;

    @Schema(description = "شناسه شهر", example = "1")
    private Long locationId;

    @Schema(description = "وضعیت آگهی", example = "APPROVED", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private String status;

    @Schema(description = "نوع مرتب‌سازی", example = "PRICE_ASC", 
           allowableValues = {"RELEVANCE", "PRICE_ASC", "PRICE_DESC", "DATE_ASC", "DATE_DESC", "POPULARITY"})
    private String sortBy = "RELEVANCE";

    @Schema(description = "شماره صفحه", example = "0")
    @Min(value = 0, message = "شماره صفحه نمی‌تواند منفی باشد")
    private Integer page = 0;

    @Schema(description = "تعداد آیتم در صفحه", example = "20")
    @Min(value = 1, message = "اندازه صفحه باید حداقل 1 باشد")
    @Max(value = 100, message = "اندازه صفحه نمی‌تواند بیش از 100 باشد")
    private Integer size = 20;

    @Schema(description = "فقط آگهی‌های ویژه", example = "false")
    private Boolean featuredOnly = false;

    @Schema(description = "فقط آگهی‌های فعال", example = "true")
    private Boolean activeOnly = true;

    @Schema(description = "برندها", example = "[\"سامسونگ\", \"اپل\"]")
    private List<String> brands;

    @Schema(description = "برچسب‌ها", example = "[\"نو\", \"دست دوم\"]")
    private List<String> tags;

    @Schema(description = "فیلتر بر اساس تاریخ ایجاد (روز)", example = "30")
    @Min(value = 1, message = "فیلتر تاریخ باید حداقل 1 روز باشد")
    private Integer daysBack;

    @Schema(description = "جستجو در فیلدهای خاص", 
           allowableValues = {"TITLE", "DESCRIPTION", "ALL"})
    private String searchIn = "ALL";

    @Schema(description = "دقت جستجو", allowableValues = {"EXACT", "PARTIAL", "FUZZY"})
    private String accuracy = "PARTIAL";

    // Helper methods
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    public boolean hasPriceRange() {
        return minPrice != null || maxPrice != null;
    }

    public boolean hasLocationFilter() {
        return locationId != null;
    }

    public boolean hasCategoryFilter() {
        return categoryId != null;
    }

    public boolean hasBrandFilter() {
        return brands != null && !brands.isEmpty();
    }

    public boolean hasTagFilter() {
        return tags != null && !tags.isEmpty();
    }

    public String getCleanKeyword() {
        return hasKeyword() ? keyword.trim().toLowerCase() : "";
    }
}
