package com.webrayan.commerce.modules.search.service;

import com.webrayan.commerce.modules.search.dto.SearchCriteria;
import com.webrayan.commerce.modules.search.dto.SearchResult;
import com.webrayan.commerce.modules.search.dto.AdSearchResult;
import com.webrayan.commerce.modules.search.dto.ProductSearchResult;

import java.util.List;
import java.util.Map;

/**
 * سرویس جستجو
 */
public interface SearchService {

    /**
     * جستجوی آگهی‌ها
     */
    SearchResult<AdSearchResult> searchAds(SearchCriteria criteria);

    /**
     * جستجوی محصولات
     */
    SearchResult<ProductSearchResult> searchProducts(SearchCriteria criteria);

    /**
     * جستجوی ترکیبی (آگهی + محصول)
     */
    Map<String, SearchResult<?>> searchAll(SearchCriteria criteria);

    /**
     * پیشنهادات جستجو (autocomplete)
     */
    List<String> getSearchSuggestions(String keyword, String type);

    /**
     * آگهی‌های مشابه
     */
    List<AdSearchResult> getSimilarAds(Long adId, int limit);

    /**
     * محصولات مشابه
     */
    List<ProductSearchResult> getSimilarProducts(Long productId, int limit);

    /**
     * آگهی‌های محبوب
     */
    List<AdSearchResult> getPopularAds(Long categoryId, int limit);

    /**
     * محصولات پرفروش
     */
    List<ProductSearchResult> getBestSellingProducts(Long categoryId, int limit);

    /**
     * آگهی‌های اخیر
     */
    List<AdSearchResult> getRecentAds(Long categoryId, int limit);

    /**
     * محصولات جدید
     */
    List<ProductSearchResult> getRecentProducts(Long categoryId, int limit);

    /**
     * محصولات ویژه
     */
    List<ProductSearchResult> getFeaturedProducts(Long categoryId, int limit);

    /**
     * محصولات تخفیف‌دار
     */
    List<ProductSearchResult> getDiscountedProducts(Long categoryId, int limit);

    /**
     * آمار جستجو
     */
    Map<String, Object> getSearchStatistics();

    /**
     * محبوب‌ترین دسته‌بندی‌ها
     */
    Map<String, Object> getPopularCategories(int limit);

    /**
     * ثبت جستجوی کاربر (برای آنالیز)
     */
    void logSearch(String keyword, String type, String userId, int resultCount);

    /**
     * کلیدواژه‌های محبوب
     */
    List<String> getTrendingKeywords(int limit);
}
