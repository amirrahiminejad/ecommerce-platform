package com.webrayan.bazaar.modules.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * نتیجه جستجو
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "نتیجه جستجو")
public class SearchResult<T> {

    @Schema(description = "آیتم‌های یافت شده")
    private List<T> items;

    @Schema(description = "تعداد کل نتایج", example = "150")
    private Long totalCount;

    @Schema(description = "تعداد صفحات", example = "8")
    private Integer totalPages;

    @Schema(description = "صفحه فعلی", example = "0")
    private Integer currentPage;

    @Schema(description = "اندازه صفحه", example = "20")
    private Integer pageSize;

    @Schema(description = "آیا صفحه بعدی وجود دارد", example = "true")
    private Boolean hasNext;

    @Schema(description = "آیا صفحه قبلی وجود دارد", example = "false")
    private Boolean hasPrevious;

    @Schema(description = "زمان جستجو به میلی‌ثانیه", example = "125")
    private Long searchTimeMs;

    @Schema(description = "کلمه کلیدی جستجو شده", example = "موبایل سامسونگ")
    private String searchKeyword;

    @Schema(description = "اطلاعات تجمیعی (aggregations)")
    private Map<String, Object> aggregations;

    @Schema(description = "پیشنهادات اصلاح املایی")
    private List<String> suggestions;

    @Schema(description = "فیلترهای اعمال شده")
    private Map<String, Object> appliedFilters;

    // Helper methods
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public boolean hasResults() {
        return !isEmpty();
    }

    public boolean isFirstPage() {
        return currentPage == 0;
    }

    public boolean isLastPage() {
        return currentPage >= totalPages - 1;
    }

    public Integer getStartIndex() {
        return currentPage * pageSize + 1;
    }

    public Integer getEndIndex() {
        return Math.min((currentPage + 1) * pageSize, totalCount.intValue());
    }

    // Static factory methods
    public static <T> SearchResult<T> empty(String keyword) {
        return SearchResult.<T>builder()
            .items(List.of())
            .totalCount(0L)
            .totalPages(0)
            .currentPage(0)
            .pageSize(20)
            .hasNext(false)
            .hasPrevious(false)
            .searchKeyword(keyword)
            .build();
    }

    public static <T> SearchResult<T> of(List<T> items, Long totalCount, Integer page, Integer size, String keyword) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        
        return SearchResult.<T>builder()
            .items(items)
            .totalCount(totalCount)
            .totalPages(totalPages)
            .currentPage(page)
            .pageSize(size)
            .hasNext(page < totalPages - 1)
            .hasPrevious(page > 0)
            .searchKeyword(keyword)
            .build();
    }
}
