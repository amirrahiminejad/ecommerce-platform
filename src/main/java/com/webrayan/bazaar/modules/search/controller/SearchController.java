package com.webrayan.bazaar.modules.search.controller;

import com.webrayan.bazaar.modules.search.service.SearchService;
import com.webrayan.bazaar.modules.search.dto.SearchCriteria;
import com.webrayan.bazaar.modules.search.dto.SearchResult;
import com.webrayan.bazaar.modules.search.dto.AdSearchResult;
import com.webrayan.bazaar.modules.search.dto.ProductSearchResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * کنترلر جستجو
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Search Management", description = "API های جستجوی آگهی‌ها و محصولات")
public class SearchController {

    private final SearchService searchService;

    @Operation(
        summary = "جستجوی آگهی‌ها",
        description = "جستجوی پیشرفته در آگهی‌ها با فیلترهای مختلف"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "جستجو با موفقیت انجام شد"),
        @ApiResponse(responseCode = "400", description = "پارامترهای ورودی نامعتبر"),
        @ApiResponse(responseCode = "500", description = "خطای سرور")
    })
    @GetMapping("/ads")
    public ResponseEntity<SearchResult<AdSearchResult>> searchAds(
            @Parameter(description = "کلمه کلیدی جستجو", example = "موبایل سامسونگ")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "شناسه دسته‌بندی", example = "1")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "شناسه موقعیت مکانی", example = "1")
            @RequestParam(required = false) Long locationId,
            
            @Parameter(description = "حداقل قیمت", example = "100000")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "حداکثر قیمت", example = "10000000")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "وضعیت آگهی", example = "APPROVED")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "نوع مرتب‌سازی", example = "RELEVANCE")
            @RequestParam(defaultValue = "RELEVANCE") String sortBy,
            
            @Parameter(description = "شماره صفحه", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            
            @Parameter(description = "تعداد آیتم در صفحه", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            
            @Parameter(description = "فقط آگهی‌های ویژه", example = "false")
            @RequestParam(defaultValue = "false") Boolean featuredOnly,
            
            @Parameter(description = "فقط آگهی‌های فعال", example = "true")
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            
            @Parameter(description = "فیلتر تاریخ (روز)", example = "30")
            @RequestParam(required = false) Integer daysBack,
            
            @Parameter(description = "جستجو در فیلد خاص", example = "ALL")
            @RequestParam(defaultValue = "ALL") String searchIn,
            
            @Parameter(description = "دقت جستجو", example = "PARTIAL")
            @RequestParam(defaultValue = "PARTIAL") String accuracy
    ) {
        log.info("Searching ads with keyword: {}", keyword);
        
        SearchCriteria criteria = SearchCriteria.builder()
            .keyword(keyword)
            .categoryId(categoryId)
            .locationId(locationId)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .status(status)
            .sortBy(sortBy)
            .page(page)
            .size(size)
            .featuredOnly(featuredOnly)
            .activeOnly(activeOnly)
            .daysBack(daysBack)
            .searchIn(searchIn)
            .accuracy(accuracy)
            .build();
        
        SearchResult<AdSearchResult> result = searchService.searchAds(criteria);
        
        // ثبت جستجو برای آنالیز
        searchService.logSearch(keyword, "ads", "anonymous", result.getTotalCount().intValue());
        
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "جستجوی محصولات",
        description = "جستجوی پیشرفته در محصولات با فیلترهای مختلف"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "جستجو با موفقیت انجام شد"),
        @ApiResponse(responseCode = "400", description = "پارامترهای ورودی نامعتبر"),
        @ApiResponse(responseCode = "500", description = "خطای سرور")
    })
    @GetMapping("/products")
    public ResponseEntity<SearchResult<ProductSearchResult>> searchProducts(
            @Parameter(description = "کلمه کلیدی جستجو", example = "گوشی موبایل")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "شناسه دسته‌بندی", example = "1")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "حداقل قیمت", example = "500000")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "حداکثر قیمت", example = "15000000")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "وضعیت محصول", example = "PUBLISHED")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "نوع مرتب‌سازی", example = "RELEVANCE")
            @RequestParam(defaultValue = "RELEVANCE") String sortBy,
            
            @Parameter(description = "شماره صفحه", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            
            @Parameter(description = "تعداد آیتم در صفحه", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            
            @Parameter(description = "فقط محصولات ویژه", example = "false")
            @RequestParam(defaultValue = "false") Boolean featuredOnly,
            
            @Parameter(description = "فقط محصولات فعال", example = "true")
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            
            @Parameter(description = "فقط محصولات موجود", example = "true")
            @RequestParam(defaultValue = "true") Boolean inStockOnly,
            
            @Parameter(description = "فیلتر تاریخ (روز)", example = "30")
            @RequestParam(required = false) Integer daysBack,
            
            @Parameter(description = "برندها")
            @RequestParam(required = false) List<String> brands,
            
            @Parameter(description = "برچسب‌ها")
            @RequestParam(required = false) List<String> tags
    ) {
        log.info("Searching products with keyword: {}", keyword);
        
        SearchCriteria criteria = SearchCriteria.builder()
            .keyword(keyword)
            .categoryId(categoryId)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .status(status)
            .sortBy(sortBy)
            .page(page)
            .size(size)
            .featuredOnly(featuredOnly)
            .activeOnly(activeOnly)
            .daysBack(daysBack)
            .brands(brands)
            .tags(tags)
            .build();
        
        SearchResult<ProductSearchResult> result = searchService.searchProducts(criteria);
        
        // ثبت جستجو برای آنالیز
        searchService.logSearch(keyword, "products", "anonymous", result.getTotalCount().intValue());
        
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "جستجوی ترکیبی",
        description = "جستجو همزمان در آگهی‌ها و محصولات"
    )
    @PostMapping("/all")
    public ResponseEntity<Map<String, SearchResult<?>>> searchAll(
            @Valid @RequestBody SearchCriteria criteria
    ) {
        log.info("Performing combined search with criteria: {}", criteria);
        
        Map<String, SearchResult<?>> results = searchService.searchAll(criteria);
        
        return ResponseEntity.ok(results);
    }

    @Operation(
        summary = "پیشنهادات جستجو",
        description = "ارائه پیشنهادات autocomplete برای جستجو"
    )
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @Parameter(description = "کلمه کلیدی", example = "موبای")
            @RequestParam String keyword,
            
            @Parameter(description = "نوع جستجو", example = "all")
            @RequestParam(defaultValue = "all") String type
    ) {
        log.debug("Getting suggestions for keyword: {}, type: {}", keyword, type);
        
        List<String> suggestions = searchService.getSearchSuggestions(keyword, type);
        
        return ResponseEntity.ok(suggestions);
    }

    @Operation(
        summary = "آگهی‌های مشابه",
        description = "دریافت آگهی‌های مشابه بر اساس یک آگهی"
    )
    @GetMapping("/ads/{adId}/similar")
    public ResponseEntity<List<AdSearchResult>> getSimilarAds(
            @Parameter(description = "شناسه آگهی", example = "1")
            @PathVariable Long adId,
            
            @Parameter(description = "تعداد نتایج", example = "5")
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        log.debug("Getting similar ads for ad ID: {}", adId);
        
        List<AdSearchResult> similarAds = searchService.getSimilarAds(adId, limit);
        
        return ResponseEntity.ok(similarAds);
    }

    @Operation(
        summary = "محصولات مشابه",
        description = "دریافت محصولات مشابه بر اساس یک محصول"
    )
    @GetMapping("/products/{productId}/similar")
    public ResponseEntity<List<ProductSearchResult>> getSimilarProducts(
            @Parameter(description = "شناسه محصول", example = "1")
            @PathVariable Long productId,
            
            @Parameter(description = "تعداد نتایج", example = "5")
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        log.debug("Getting similar products for product ID: {}", productId);
        
        List<ProductSearchResult> similarProducts = searchService.getSimilarProducts(productId, limit);
        
        return ResponseEntity.ok(similarProducts);
    }

    @Operation(
        summary = "آگهی‌های محبوب",
        description = "دریافت آگهی‌های محبوب"
    )
    @GetMapping("/ads/popular")
    public ResponseEntity<List<AdSearchResult>> getPopularAds(
            @Parameter(description = "شناسه دسته‌بندی")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.debug("Getting popular ads for category: {}", categoryId);
        
        List<AdSearchResult> popularAds = searchService.getPopularAds(categoryId, limit);
        
        return ResponseEntity.ok(popularAds);
    }

    @Operation(
        summary = "محصولات پرفروش",
        description = "دریافت محصولات پرفروش"
    )
    @GetMapping("/products/bestsellers")
    public ResponseEntity<List<ProductSearchResult>> getBestSellingProducts(
            @Parameter(description = "شناسه دسته‌بندی")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.debug("Getting best selling products for category: {}", categoryId);
        
        List<ProductSearchResult> bestSellers = searchService.getBestSellingProducts(categoryId, limit);
        
        return ResponseEntity.ok(bestSellers);
    }

    @Operation(
        summary = "آگهی‌های اخیر",
        description = "دریافت آگهی‌های اخیر"
    )
    @GetMapping("/ads/recent")
    public ResponseEntity<List<AdSearchResult>> getRecentAds(
            @Parameter(description = "شناسه دسته‌بندی")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<AdSearchResult> recentAds = searchService.getRecentAds(categoryId, limit);
        return ResponseEntity.ok(recentAds);
    }

    @Operation(
        summary = "محصولات جدید",
        description = "دریافت محصولات جدید"
    )
    @GetMapping("/products/recent")
    public ResponseEntity<List<ProductSearchResult>> getRecentProducts(
            @Parameter(description = "شناسه دسته‌بندی")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<ProductSearchResult> recentProducts = searchService.getRecentProducts(categoryId, limit);
        return ResponseEntity.ok(recentProducts);
    }

    @Operation(
        summary = "محصولات ویژه",
        description = "دریافت محصولات ویژه"
    )
    @GetMapping("/products/featured")
    public ResponseEntity<List<ProductSearchResult>> getFeaturedProducts(
            @Parameter(description = "شناسه دسته‌بندی")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<ProductSearchResult> featuredProducts = searchService.getFeaturedProducts(categoryId, limit);
        return ResponseEntity.ok(featuredProducts);
    }

    @Operation(
        summary = "محصولات تخفیف‌دار",
        description = "دریافت محصولات با تخفیف"
    )
    @GetMapping("/products/discounted")
    public ResponseEntity<List<ProductSearchResult>> getDiscountedProducts(
            @Parameter(description = "شناسه دسته‌بندی")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<ProductSearchResult> discountedProducts = searchService.getDiscountedProducts(categoryId, limit);
        return ResponseEntity.ok(discountedProducts);
    }

    @Operation(
        summary = "آمار جستجو",
        description = "دریافت آمار کلی جستجو"
    )
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSearchStatistics() {
        log.debug("Getting search statistics");
        
        Map<String, Object> statistics = searchService.getSearchStatistics();
        
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "دسته‌بندی‌های محبوب",
        description = "دریافت محبوب‌ترین دسته‌بندی‌ها"
    )
    @GetMapping("/categories/popular")
    public ResponseEntity<Map<String, Object>> getPopularCategories(
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.debug("Getting popular categories");
        
        Map<String, Object> popularCategories = searchService.getPopularCategories(limit);
        
        return ResponseEntity.ok(popularCategories);
    }

    @Operation(
        summary = "کلیدواژه‌های محبوب",
        description = "دریافت کلیدواژه‌های پربازدید"
    )
    @GetMapping("/keywords/trending")
    public ResponseEntity<List<String>> getTrendingKeywords(
            @Parameter(description = "تعداد نتایج", example = "10")
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.debug("Getting trending keywords");
        
        List<String> trendingKeywords = searchService.getTrendingKeywords(limit);
        
        return ResponseEntity.ok(trendingKeywords);
    }
}
