package com.webrayan.store.modules.search.service.impl;

import com.webrayan.store.modules.search.service.SearchService;
import com.webrayan.store.modules.search.dto.SearchCriteria;
import com.webrayan.store.modules.search.dto.SearchResult;
import com.webrayan.store.modules.search.dto.AdSearchResult;
import com.webrayan.store.modules.search.dto.ProductSearchResult;
import com.webrayan.store.modules.search.repository.AdSearchRepository;
import com.webrayan.store.modules.search.repository.ProductSearchRepository;
import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.catalog.entity.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * پیاده‌سازی سرویس جستجو
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final AdSearchRepository adSearchRepository;
    private final ProductSearchRepository productSearchRepository;

    @Override
    public SearchResult<AdSearchResult> searchAds(SearchCriteria criteria) {
        log.info("Searching ads with criteria: {}", criteria);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // تبدیل معیارها
            String keyword = criteria.getCleanKeyword();
            LocalDateTime dateFrom = criteria.getDaysBack() != null ? 
                LocalDateTime.now().minusDays(criteria.getDaysBack()) : null;
            
            // صفحه‌بندی
            Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
            
            // جستجو
            Page<Ad> adPage = adSearchRepository.searchAds(
                keyword.isEmpty() ? null : keyword,
                criteria.getCategoryId(),
                criteria.getLocationId(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getStatus(),
                criteria.getActiveOnly(),
                criteria.getFeaturedOnly(),
                criteria.getDaysBack(),
                dateFrom,
                criteria.getSortBy(),
                pageable
            );
            
            // تبدیل به DTO
            List<AdSearchResult> results = adPage.getContent().stream()
                .map(this::convertToAdSearchResult)
                .collect(Collectors.toList());
            
            // ایجاد نتیجه
            SearchResult<AdSearchResult> searchResult = SearchResult.of(
                results,
                adPage.getTotalElements(),
                criteria.getPage(),
                criteria.getSize(),
                keyword
            );
            
            // اضافه کردن زمان جستجو
            searchResult.setSearchTimeMs(System.currentTimeMillis() - startTime);
            
            // اضافه کردن پیشنهادات اگر نتیجه‌ای نیافت
            if (results.isEmpty() && !keyword.isEmpty()) {
                List<String> suggestions = adSearchRepository.findSimilarTitles(keyword);
                searchResult.setSuggestions(suggestions);
            }
            
            log.info("Found {} ads in {} ms", results.size(), searchResult.getSearchTimeMs());
            return searchResult;
            
        } catch (Exception e) {
            log.error("Error searching ads", e);
            return SearchResult.empty(criteria.getKeyword());
        }
    }

    @Override
    public SearchResult<ProductSearchResult> searchProducts(SearchCriteria criteria) {
        log.info("Searching products with criteria: {}", criteria);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // تبدیل معیارها
            String keyword = criteria.getCleanKeyword();
            LocalDateTime dateFrom = criteria.getDaysBack() != null ? 
                LocalDateTime.now().minusDays(criteria.getDaysBack()) : null;
            
            // صفحه‌بندی
            Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
            
            // جستجو
            Page<Product> productPage = productSearchRepository.searchProducts(
                keyword.isEmpty() ? null : keyword,
                criteria.getCategoryId(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getStatus(),
                criteria.getActiveOnly(),
                criteria.getFeaturedOnly(),
                true, // inStockOnly
                null, // digitalOnly
                criteria.getDaysBack(),
                dateFrom,
                criteria.getSortBy(),
                pageable
            );
            
            // تبدیل به DTO
            List<ProductSearchResult> results = productPage.getContent().stream()
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
            
            // ایجاد نتیجه
            SearchResult<ProductSearchResult> searchResult = SearchResult.of(
                results,
                productPage.getTotalElements(),
                criteria.getPage(),
                criteria.getSize(),
                keyword
            );
            
            // اضافه کردن زمان جستجو
            searchResult.setSearchTimeMs(System.currentTimeMillis() - startTime);
            
            // اضافه کردن پیشنهادات اگر نتیجه‌ای نیافت
            if (results.isEmpty() && !keyword.isEmpty()) {
                List<String> suggestions = productSearchRepository.findSimilarProductNames(keyword);
                searchResult.setSuggestions(suggestions);
            }
            
            log.info("Found {} products in {} ms", results.size(), searchResult.getSearchTimeMs());
            return searchResult;
            
        } catch (Exception e) {
            log.error("Error searching products", e);
            return SearchResult.empty(criteria.getKeyword());
        }
    }

    @Override
    public Map<String, SearchResult<?>> searchAll(SearchCriteria criteria) {
        log.info("Performing combined search with criteria: {}", criteria);
        
        Map<String, SearchResult<?>> results = new HashMap<>();
        
        // جستجوی موازی
        try {
            SearchResult<AdSearchResult> adResults = searchAds(criteria);
            SearchResult<ProductSearchResult> productResults = searchProducts(criteria);
            
            results.put("ads", adResults);
            results.put("products", productResults);
            
            log.info("Combined search completed - Ads: {}, Products: {}", 
                adResults.getTotalCount(), productResults.getTotalCount());
                
        } catch (Exception e) {
            log.error("Error in combined search", e);
            results.put("ads", SearchResult.empty(criteria.getKeyword()));
            results.put("products", SearchResult.empty(criteria.getKeyword()));
        }
        
        return results;
    }

    @Override
    public List<String> getSearchSuggestions(String keyword, String type) {
        log.debug("Getting search suggestions for keyword: {}, type: {}", keyword, type);
        
        if (keyword == null || keyword.trim().length() < 2) {
            return Collections.emptyList();
        }
        
        try {
            switch (type.toLowerCase()) {
                case "ads":
                    return adSearchRepository.findSimilarTitles(keyword.trim());
                case "products":
                    return productSearchRepository.findSimilarProductNames(keyword.trim());
                case "all":
                default:
                    List<String> suggestions = new ArrayList<>();
                    suggestions.addAll(adSearchRepository.findSimilarTitles(keyword.trim()));
                    suggestions.addAll(productSearchRepository.findSimilarProductNames(keyword.trim()));
                    return suggestions.stream().distinct().limit(10).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting search suggestions", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AdSearchResult> getSimilarAds(Long adId, int limit) {
        log.debug("Getting similar ads for ad ID: {}", adId);
        
        try {
            // ابتدا آگهی اصلی را بیاب
            Optional<Ad> originalAd = adSearchRepository.findById(adId);
            if (originalAd.isEmpty()) {
                return Collections.emptyList();
            }
            
            Ad ad = originalAd.get();
            
            // محاسبه محدوده قیمت
            Double price = ad.getPrice();
            if (price == null) price = 0.0;
            
            Double minPrice = price * 0.7; // 30% کمتر
            Double maxPrice = price * 1.3; // 30% بیشتر
            
            // جستجوی آگهی‌های مشابه
            List<Ad> similarAds = adSearchRepository.findSimilarAds(
                ad.getCategory() != null ? ad.getCategory().getId() : null,
                adId,
                java.math.BigDecimal.valueOf(price),
                java.math.BigDecimal.valueOf(minPrice),
                java.math.BigDecimal.valueOf(maxPrice)
            );
            
            return similarAds.stream()
                .limit(limit)
                .map(this::convertToAdSearchResult)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting similar ads for ID: {}", adId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductSearchResult> getSimilarProducts(Long productId, int limit) {
        log.debug("Getting similar products for product ID: {}", productId);
        
        try {
            // ابتدا محصول اصلی را بیاب
            Optional<Product> originalProduct = productSearchRepository.findById(productId);
            if (originalProduct.isEmpty()) {
                return Collections.emptyList();
            }
            
            Product product = originalProduct.get();
            
            // محاسبه محدوده قیمت
            java.math.BigDecimal price = product.getPrice();
            if (price == null) price = java.math.BigDecimal.ZERO;
            
            java.math.BigDecimal minPrice = price.multiply(java.math.BigDecimal.valueOf(0.7));
            java.math.BigDecimal maxPrice = price.multiply(java.math.BigDecimal.valueOf(1.3));
            
            // جستجوی محصولات مشابه
            List<Product> similarProducts = productSearchRepository.findSimilarProducts(
                product.getCategory() != null ? product.getCategory().getId() : null,
                productId,
                price,
                minPrice,
                maxPrice
            );
            
            return similarProducts.stream()
                .limit(limit)
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting similar products for ID: {}", productId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AdSearchResult> getPopularAds(Long categoryId, int limit) {
        log.debug("Getting popular ads for category: {}", categoryId);
        
        try {
            LocalDateTime dateFrom = LocalDateTime.now().minusDays(30); // آخرین 30 روز
            List<Ad> popularAds = adSearchRepository.findPopularAds(dateFrom, limit);
            
            return popularAds.stream()
                .map(this::convertToAdSearchResult)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting popular ads", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductSearchResult> getBestSellingProducts(Long categoryId, int limit) {
        log.debug("Getting best selling products for category: {}", categoryId);
        
        try {
            LocalDateTime dateFrom = LocalDateTime.now().minusDays(30); // آخرین 30 روز
            List<Product> bestSellers = productSearchRepository.findBestSellingProducts(categoryId, dateFrom, limit);
            
            return bestSellers.stream()
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting best selling products", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AdSearchResult> getRecentAds(Long categoryId, int limit) {
        try {
            List<Ad> recentAds = adSearchRepository.findRecentAds(categoryId, limit);
            return recentAds.stream()
                .map(this::convertToAdSearchResult)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting recent ads", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductSearchResult> getRecentProducts(Long categoryId, int limit) {
        try {
            List<Product> recentProducts = productSearchRepository.findRecentProducts(categoryId, limit);
            return recentProducts.stream()
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting recent products", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductSearchResult> getFeaturedProducts(Long categoryId, int limit) {
        try {
            List<Product> featuredProducts = productSearchRepository.findFeaturedProducts(categoryId, limit);
            return featuredProducts.stream()
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting featured products", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductSearchResult> getDiscountedProducts(Long categoryId, int limit) {
        try {
            List<Product> discountedProducts = productSearchRepository.findDiscountedProducts(categoryId, limit);
            return discountedProducts.stream()
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting discounted products", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getSearchStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            LocalDateTime dateFrom = LocalDateTime.now().minusDays(30);
            
            // آمار آگهی‌ها
            Object[] adStats = adSearchRepository.getSearchStatistics(dateFrom);
            if (adStats != null && adStats.length > 0) {
                stats.put("totalAds", adStats[0]);
                stats.put("approvedAds", adStats[1]);
                stats.put("featuredAds", adStats[2]);
                stats.put("recentAds", adStats[3]);
            }
            
            // آمار محصولات
            Object[] productStats = productSearchRepository.getProductStatistics(dateFrom);
            if (productStats != null && productStats.length > 0) {
                stats.put("totalProducts", productStats[0]);
                stats.put("publishedProducts", productStats[1]);
                stats.put("featuredProducts", productStats[2]);
                stats.put("inStockProducts", productStats[3]);
                stats.put("discountedProducts", productStats[4]);
                stats.put("recentProducts", productStats[5]);
            }
            
        } catch (Exception e) {
            log.error("Error getting search statistics", e);
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> getPopularCategories(int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LocalDateTime dateFrom = LocalDateTime.now().minusDays(30);
            
            // دسته‌بندی‌های محبوب آگهی‌ها
            List<Object[]> popularAdCategories = adSearchRepository.getPopularCategories(dateFrom, limit);
            result.put("adCategories", popularAdCategories);
            
            // دسته‌بندی‌های محبوب محصولات
            List<Object[]> popularProductCategories = productSearchRepository.getPopularProductCategories(dateFrom, limit);
            result.put("productCategories", popularProductCategories);
            
        } catch (Exception e) {
            log.error("Error getting popular categories", e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void logSearch(String keyword, String type, String userId, int resultCount) {
        // TODO: پیاده‌سازی لاگ جستجو برای آنالیز
        log.info("Search logged - Keyword: {}, Type: {}, User: {}, Results: {}", 
            keyword, type, userId, resultCount);
    }

    @Override
    public List<String> getTrendingKeywords(int limit) {
        // TODO: پیاده‌سازی کلیدواژه‌های محبوب
        return Collections.emptyList();
    }

    // Helper methods
    private AdSearchResult convertToAdSearchResult(Ad ad) {
        return AdSearchResult.builder()
            .id(ad.getId())
            .title(ad.getTitle())
            .shortDescription(truncateText(ad.getDescription(), 150))
            .price(ad.getPrice() != null ? java.math.BigDecimal.valueOf(ad.getPrice()) : null)
            .status(ad.getStatus().toString())
            .categoryName(ad.getCategory() != null ? ad.getCategory().getName() : null)
            .categoryId(ad.getCategory() != null ? ad.getCategory().getId() : null)
            .locationName(ad.getLocation() != null ? ad.getLocation().getCity() : null)
            .locationId(ad.getLocation() != null ? Long.valueOf(ad.getLocation().getId()) : null)
            .sellerName(ad.getUser() != null ? ad.getUser().getUsername() : null)
            .sellerId(ad.getUser() != null ? ad.getUser().getId() : null)
            .viewsCount(ad.getViewsCount())
            .isFeatured(ad.getIsFeatured())
            .isActive(ad.getIsActive())
            .createdAt(ad.getCreatedAt())
            .updatedAt(ad.getUpdatedAt())
            .build();
    }

    private ProductSearchResult convertToProductSearchResult(Product product) {
        return ProductSearchResult.builder()
            .id(product.getId())
            .name(product.getName())
            .shortDescription(product.getShortDescription())
            .price(product.getPrice())
            .discountPrice(product.getDiscountPrice())
            .status(product.getStatus().toString())
            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .sellerName(product.getSeller() != null ? product.getSeller().getUsername() : null)
            .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
            .stockQuantity(product.getStockQuantity())
            .sku(product.getSku())
            .viewsCount(product.getViewsCount())
            .salesCount(product.getSalesCount())
            .isFeatured(product.getIsFeatured())
            .isActive(product.getIsActive())
            .isDigital(product.getIsDigital())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
