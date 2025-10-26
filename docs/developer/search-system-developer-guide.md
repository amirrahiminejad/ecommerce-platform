# راهنمای توسعه‌دهنده - سیستم جستجوی پرشیا بازار

## مقدمه

این مستند راهنمای کاملی برای توسعه‌دهندگان تیم پرشیا بازار جهت کار با سیستم جستجوی جدید ارائه می‌دهد. این سیستم بر پایه PostgreSQL Full-Text Search ساخته شده و قابلیت مهاجرت به Elasticsearch را دارد.

## معماری کلی

### ساختار لایه‌ای

```
┌─────────────────────────────────────────────────────────────┐
│                 Presentation Layer                          │
│              SearchController (REST API)                    │
├─────────────────────────────────────────────────────────────┤
│                  Service Layer                              │
│            SearchService (Business Logic)                   │
├─────────────────────────────────────────────────────────────┤
│                Repository Layer                             │
│     AdSearchRepository + ProductSearchRepository            │
├─────────────────────────────────────────────────────────────┤
│                   Data Layer                                │
│           PostgreSQL + Full-Text Search Indexes            │
└─────────────────────────────────────────────────────────────┘
```

### کامپوننت‌های اصلی

1. **DTOs**: کلاس‌های انتقال داده
2. **Repository**: لایه دسترسی به داده
3. **Service**: منطق تجاری
4. **Controller**: API endpoints
5. **Configuration**: تنظیمات سیستم

## DTOs (Data Transfer Objects)

### SearchCriteria.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    
    // کلمه کلیدی جستجو
    @Size(max = 255, message = "کلمه کلیدی نمی‌تواند بیش از 255 کاراکتر باشد")
    private String keyword;
    
    // فیلترهای قیمت
    @Min(value = 0, message = "حداقل قیمت نمی‌تواند منفی باشد")
    private BigDecimal minPrice;
    
    @Min(value = 0, message = "حداکثر قیمت نمی‌تواند منفی باشد")
    private BigDecimal maxPrice;
    
    // فیلتر دسته‌بندی
    private Long categoryId;
    
    // فیلتر موقعیت جغرافیایی
    private Long locationId;
    
    // فیلترهای وضعیت
    private Boolean featuredOnly = false;
    private Boolean activeOnly = true;
    private Boolean inStockOnly = false;
    
    // صفحه‌بندی
    @NotNull
    @Min(value = 0, message = "شماره صفحه نمی‌تواند منفی باشد")
    private Integer page = 0;
    
    @NotNull
    @Min(value = 1, message = "اندازه صفحه باید حداقل 1 باشد")
    @Max(value = 100, message = "حداکثر 100 آیتم در هر صفحه مجاز است")
    private Integer size = 20;
    
    // مرتب‌سازی
    @NotNull
    private SortType sortBy = SortType.RELEVANCE;
    
    // فیلترهای پیشرفته
    private List<String> brands;
    private List<String> tags;
    private Integer daysBack;
    private SearchAccuracy accuracy = SearchAccuracy.EXACT;
    
    // متدهای کمکی
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
    
    public boolean hasDateFilter() {
        return daysBack != null && daysBack > 0;
    }
    
    // Validation method
    @AssertTrue(message = "حداقل قیمت نمی‌تواند بیشتر از حداکثر قیمت باشد")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice.compareTo(maxPrice) <= 0;
    }
}
```

**نکات مهم:**
- همیشه validation annotations استفاده کنید
- متدهای کمکی برای بررسی وجود فیلترها اضافه کنید
- Builder pattern برای ساخت آسان‌تر استفاده کنید

### SearchResult DTOs

```java
// نتیجه جستجوی آگهی
@Data
@Builder
public class AdSearchResult {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // اطلاعات دسته‌بندی
    private Long categoryId;
    private String categoryName;
    
    // اطلاعات موقعیت
    private Long locationId;
    private String locationName;
    
    // اطلاعات کاربر
    private Long userId;
    private String userName;
    
    // آمار
    private Integer viewCount;
    private Integer favoriteCount;
    
    // متادیتا
    private List<String> images;
    private List<String> tags;
    private Map<String, Object> attributes;
    
    // امتیاز ارتباط (برای جستجوی متنی)
    private Double relevanceScore;
    
    // نشان‌گرها
    private Boolean isFeatured;
    private Boolean isUrgent;
    private Boolean isVerified;
}

// نتیجه جستجوی محصول
@Data
@Builder
public class ProductSearchResult {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String currency;
    private String status;
    private Integer stockQuantity;
    private String brand;
    
    // اطلاعات دسته‌بندی
    private Long categoryId;
    private String categoryName;
    
    // آمار فروش
    private Integer salesCount;
    private Double rating;
    private Integer reviewCount;
    
    // تصاویر
    private List<String> images;
    private String mainImage;
    
    // ویژگی‌ها
    private Map<String, Object> specifications;
    private List<String> tags;
    
    // امتیاز ارتباط
    private Double relevanceScore;
    
    // نشان‌گرها
    private Boolean isFeatured;
    private Boolean isOnSale;
    private Boolean isInStock;
    private Boolean isBestseller;
}

// صفحه نتایج
@Data
@Builder
public class SearchResultPage<T> {
    private List<T> items;
    private Long totalCount;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
    
    // متادیتا
    private String searchId; // برای tracking
    private Long searchTimeMs;
    private Map<String, Object> aggregations; // برای faceted search
    
    public SearchResultPage(List<T> items, Long totalCount, Integer currentPage, Integer pageSize) {
        this.items = items;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }
}
```

## Repository Layer

### AdSearchRepository.java

```java
@Repository
public interface AdSearchRepository extends JpaRepository<Ad, Long> {
    
    /**
     * جستجوی Full-Text با پشتیبانی از زبان فارسی
     */
    @Query(value = """
        SELECT a.*, 
               ts_rank(to_tsvector('persian', 
                   COALESCE(a.title, '') || ' ' || 
                   COALESCE(a.description, '') || ' ' || 
                   COALESCE(a.meta_keywords, '')
               ), plainto_tsquery('persian', :keyword)) as rank
        FROM ads a
        WHERE (:keyword IS NULL OR 
               to_tsvector('persian', 
                   COALESCE(a.title, '') || ' ' || 
                   COALESCE(a.description, '') || ' ' || 
                   COALESCE(a.meta_keywords, '')
               ) @@ plainto_tsquery('persian', :keyword))
          AND (:categoryId IS NULL OR a.category_id = :categoryId)
          AND (:locationId IS NULL OR a.location_id = :locationId)
          AND (:minPrice IS NULL OR a.price >= :minPrice)
          AND (:maxPrice IS NULL OR a.price <= :maxPrice)
          AND (:activeOnly = false OR a.status = 'ACTIVE')
          AND (:featuredOnly = false OR a.is_featured = true)
          AND (:daysBack IS NULL OR a.created_at >= NOW() - INTERVAL ':daysBack days')
        ORDER BY 
          CASE WHEN :sortBy = 'RELEVANCE' THEN rank END DESC,
          CASE WHEN :sortBy = 'PRICE_ASC' THEN a.price END ASC,
          CASE WHEN :sortBy = 'PRICE_DESC' THEN a.price END DESC,
          CASE WHEN :sortBy = 'DATE_DESC' THEN a.created_at END DESC,
          CASE WHEN :sortBy = 'DATE_ASC' THEN a.created_at END ASC,
          a.created_at DESC
        """, nativeQuery = true)
    Page<Ad> findByAdvancedSearch(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("locationId") Long locationId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("activeOnly") Boolean activeOnly,
        @Param("featuredOnly") Boolean featuredOnly,
        @Param("daysBack") Integer daysBack,
        @Param("sortBy") String sortBy,
        Pageable pageable
    );
    
    /**
     * جستجوی Fuzzy با استفاده از trigram
     */
    @Query(value = """
        SELECT a.*, similarity(a.title, :keyword) as sim
        FROM ads a
        WHERE similarity(a.title, :keyword) > :threshold
           OR similarity(a.description, :keyword) > :threshold
        ORDER BY sim DESC
        """, nativeQuery = true)
    Page<Ad> findByFuzzySearch(
        @Param("keyword") String keyword,
        @Param("threshold") Double threshold,
        Pageable pageable
    );
    
    /**
     * پیدا کردن آگهی‌های مشابه
     */
    @Query(value = """
        SELECT DISTINCT a.*
        FROM ads a
        WHERE a.id != :adId
          AND a.category_id = :categoryId
          AND a.status = 'ACTIVE'
          AND (
            similarity(a.title, :title) > 0.3
            OR a.price BETWEEN :minPrice AND :maxPrice
            OR EXISTS (
              SELECT 1 FROM ad_tags at1 
              JOIN ad_tags at2 ON at1.tag_id = at2.tag_id
              WHERE at1.ad_id = :adId AND at2.ad_id = a.id
            )
          )
        ORDER BY 
          similarity(a.title, :title) DESC,
          ABS(a.price - :targetPrice) ASC,
          a.created_at DESC
        """, nativeQuery = true)
    List<Ad> findSimilarAds(
        @Param("adId") Long adId,
        @Param("categoryId") Long categoryId,
        @Param("title") String title,
        @Param("targetPrice") BigDecimal targetPrice,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    /**
     * آگهی‌های محبوب بر اساس تعامل کاربران
     */
    @Query(value = """
        SELECT a.*, 
               (COALESCE(a.view_count, 0) * 0.4 + 
                COALESCE(a.favorite_count, 0) * 0.6) as popularity_score
        FROM ads a
        WHERE a.status = 'ACTIVE'
          AND (:categoryId IS NULL OR a.category_id = :categoryId)
          AND a.created_at >= NOW() - INTERVAL '30 days'
        ORDER BY popularity_score DESC, a.created_at DESC
        """, nativeQuery = true)
    List<Ad> findPopularAds(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * پیشنهادات جستجو
     */
    @Query(value = """
        SELECT DISTINCT a.title
        FROM ads a
        WHERE a.title ILIKE :keyword || '%'
          AND a.status = 'ACTIVE'
        ORDER BY a.title
        LIMIT 10
        """, nativeQuery = true)
    List<String> findSearchSuggestions(@Param("keyword") String keyword);
    
    /**
     * آمار جستجو برای دسته‌بندی
     */
    @Query(value = """
        SELECT 
            c.name as category_name,
            COUNT(a.id) as ad_count,
            AVG(a.price) as avg_price,
            MAX(a.price) as max_price,
            MIN(a.price) as min_price
        FROM categories c
        LEFT JOIN ads a ON c.id = a.category_id AND a.status = 'ACTIVE'
        WHERE (:categoryId IS NULL OR c.id = :categoryId)
        GROUP BY c.id, c.name
        ORDER BY ad_count DESC
        """, nativeQuery = true)
    List<Object[]> getCategoryStatistics(@Param("categoryId") Long categoryId);
}
```

**نکات کدنویسی Repository:**

1. **استفاده از Native Query**: برای عملکرد بهتر در جستجوهای پیچیده
2. **پشتیبانی از زبان فارسی**: استفاده از `'persian'` در تنظیمات PostgreSQL
3. **Parameterized Queries**: همیشه از `@Param` استفاده کنید
4. **Null Safety**: بررسی null بودن پارامترها در query
5. **Performance**: استفاده از INDEX و بهینه‌سازی WHERE clause

### ProductSearchRepository.java

```java
@Repository
public interface ProductSearchRepository extends JpaRepository<Product, Long> {
    
    /**
     * جستجوی پیشرفته محصولات
     */
    @Query(value = """
        SELECT p.*, 
               ts_rank(to_tsvector('persian', 
                   COALESCE(p.name, '') || ' ' || 
                   COALESCE(p.description, '') || ' ' || 
                   COALESCE(p.brand, '') || ' ' ||
                   COALESCE(p.meta_keywords, '')
               ), plainto_tsquery('persian', :keyword)) as rank
        FROM products p
        WHERE (:keyword IS NULL OR 
               to_tsvector('persian', 
                   COALESCE(p.name, '') || ' ' || 
                   COALESCE(p.description, '') || ' ' || 
                   COALESCE(p.brand, '') || ' ' ||
                   COALESCE(p.meta_keywords, '')
               ) @@ plainto_tsquery('persian', :keyword))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:activeOnly = false OR p.status = 'ACTIVE')
          AND (:inStockOnly = false OR p.stock_quantity > 0)
          AND (:featuredOnly = false OR p.is_featured = true)
          AND (:brands IS NULL OR p.brand = ANY(CAST(:brands AS text[])))
        ORDER BY 
          CASE WHEN :sortBy = 'RELEVANCE' THEN rank END DESC,
          CASE WHEN :sortBy = 'PRICE_ASC' THEN p.price END ASC,
          CASE WHEN :sortBy = 'PRICE_DESC' THEN p.price END DESC,
          CASE WHEN :sortBy = 'SALES' THEN p.sales_count END DESC,
          CASE WHEN :sortBy = 'RATING' THEN p.rating END DESC,
          CASE WHEN :sortBy = 'DATE_DESC' THEN p.created_at END DESC,
          p.created_at DESC
        """, nativeQuery = true)
    Page<Product> findByAdvancedSearch(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("activeOnly") Boolean activeOnly,
        @Param("inStockOnly") Boolean inStockOnly,
        @Param("featuredOnly") Boolean featuredOnly,
        @Param("brands") String[] brands,
        @Param("sortBy") String sortBy,
        Pageable pageable
    );
    
    /**
     * پرفروش‌ترین محصولات
     */
    @Query(value = """
        SELECT p.*
        FROM products p
        WHERE p.status = 'ACTIVE'
          AND p.stock_quantity > 0
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND p.created_at >= NOW() - INTERVAL '90 days'
        ORDER BY 
          p.sales_count DESC,
          p.rating DESC,
          p.created_at DESC
        """, nativeQuery = true)
    List<Product> findBestsellers(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * محصولات تخفیف‌دار
     */
    @Query(value = """
        SELECT p.*, 
               ((p.price - p.discount_price) / p.price * 100) as discount_percentage
        FROM products p
        WHERE p.status = 'ACTIVE'
          AND p.stock_quantity > 0
          AND p.discount_price IS NOT NULL
          AND p.discount_price < p.price
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY discount_percentage DESC, p.created_at DESC
        """, nativeQuery = true)
    List<Product> findDiscountedProducts(@Param("categoryId") Long categoryId, Pageable pageable);
}
```

## Service Layer

### SearchService Interface

```java
public interface SearchService {
    
    /**
     * جستجوی آگهی‌ها
     */
    SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria);
    
    /**
     * جستجوی محصولات
     */
    SearchResultPage<ProductSearchResult> searchProducts(SearchCriteria criteria);
    
    /**
     * جستجوی ترکیبی
     */
    CombinedSearchResult searchAll(SearchCriteria criteria);
    
    /**
     * پیدا کردن آگهی‌های مشابه
     */
    List<AdSearchResult> findSimilarAds(Long adId, Integer limit);
    
    /**
     * پیدا کردن محصولات مشابه
     */
    List<ProductSearchResult> findSimilarProducts(Long productId, Integer limit);
    
    /**
     * آگهی‌های محبوب
     */
    List<AdSearchResult> getPopularAds(Long categoryId, Integer limit);
    
    /**
     * محصولات پرفروش
     */
    List<ProductSearchResult> getBestsellers(Long categoryId, Integer limit);
    
    /**
     * پیشنهادات جستجو
     */
    List<String> getSearchSuggestions(String keyword, SearchType type);
    
    /**
     * آمار جستجو
     */
    SearchStatistics getSearchStatistics();
}
```

### SearchServiceImpl

```java
@Service
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {
    
    private final AdSearchRepository adSearchRepository;
    private final ProductSearchRepository productSearchRepository;
    private final SearchAnalyticsService analyticsService;
    
    // تنظیمات
    @Value("${app.search.fuzzy-threshold:0.3}")
    private Double fuzzyThreshold;
    
    @Value("${app.search.cache-ttl:300}")
    private Integer cacheTtl;
    
    @Override
    @Cacheable(value = "search-ads", key = "#criteria.hashCode()")
    public SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria) {
        log.info("جستجوی آگهی‌ها با معیارهای: {}", criteria);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // بررسی اعتبار معیارها
            validateSearchCriteria(criteria);
            
            // ساخت Pageable
            Pageable pageable = buildPageable(criteria);
            
            // اجرای جستجو
            Page<Ad> ads;
            if (criteria.hasKeyword() && criteria.getAccuracy() == SearchAccuracy.FUZZY) {
                ads = adSearchRepository.findByFuzzySearch(
                    criteria.getKeyword(), 
                    fuzzyThreshold, 
                    pageable
                );
            } else {
                ads = adSearchRepository.findByAdvancedSearch(
                    criteria.getKeyword(),
                    criteria.getCategoryId(),
                    criteria.getLocationId(),
                    criteria.getMinPrice(),
                    criteria.getMaxPrice(),
                    criteria.getActiveOnly(),
                    criteria.getFeaturedOnly(),
                    criteria.getDaysBack(),
                    criteria.getSortBy().name(),
                    pageable
                );
            }
            
            // تبدیل به DTO
            List<AdSearchResult> results = ads.getContent().stream()
                .map(this::convertToAdSearchResult)
                .collect(Collectors.toList());
            
            // ساخت نتیجه نهایی
            SearchResultPage<AdSearchResult> resultPage = new SearchResultPage<>(
                results,
                ads.getTotalElements(),
                criteria.getPage(),
                criteria.getSize()
            );
            
            // ثبت آمار
            long searchTime = System.currentTimeMillis() - startTime;
            resultPage.setSearchTimeMs(searchTime);
            analyticsService.recordSearch(criteria, results.size(), searchTime);
            
            log.info("جستجوی آگهی‌ها کامل شد. {} نتیجه در {} میلی‌ثانیه", 
                results.size(), searchTime);
            
            return resultPage;
            
        } catch (Exception e) {
            log.error("خطا در جستجوی آگهی‌ها: {}", e.getMessage(), e);
            throw new SearchException("خطا در اجرای جستجو", e);
        }
    }
    
    @Override
    @Cacheable(value = "search-products", key = "#criteria.hashCode()")
    public SearchResultPage<ProductSearchResult> searchProducts(SearchCriteria criteria) {
        log.info("جستجوی محصولات با معیارهای: {}", criteria);
        
        long startTime = System.currentTimeMillis();
        
        try {
            validateSearchCriteria(criteria);
            
            Pageable pageable = buildPageable(criteria);
            
            // تبدیل برندها به آرایه
            String[] brands = null;
            if (criteria.hasBrandFilter()) {
                brands = criteria.getBrands().toArray(new String[0]);
            }
            
            Page<Product> products = productSearchRepository.findByAdvancedSearch(
                criteria.getKeyword(),
                criteria.getCategoryId(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getActiveOnly(),
                criteria.getInStockOnly(),
                criteria.getFeaturedOnly(),
                brands,
                criteria.getSortBy().name(),
                pageable
            );
            
            List<ProductSearchResult> results = products.getContent().stream()
                .map(this::convertToProductSearchResult)
                .collect(Collectors.toList());
            
            SearchResultPage<ProductSearchResult> resultPage = new SearchResultPage<>(
                results,
                products.getTotalElements(),
                criteria.getPage(),
                criteria.getSize()
            );
            
            long searchTime = System.currentTimeMillis() - startTime;
            resultPage.setSearchTimeMs(searchTime);
            analyticsService.recordSearch(criteria, results.size(), searchTime);
            
            log.info("جستجوی محصولات کامل شد. {} نتیجه در {} میلی‌ثانیه", 
                results.size(), searchTime);
            
            return resultPage;
            
        } catch (Exception e) {
            log.error("خطا در جستجوی محصولات: {}", e.getMessage(), e);
            throw new SearchException("خطا در اجرای جستجو", e);
        }
    }
    
    // متدهای کمکی
    private void validateSearchCriteria(SearchCriteria criteria) {
        if (criteria.getSize() > 100) {
            throw new IllegalArgumentException("حداکثر 100 آیتم در هر صفحه مجاز است");
        }
        
        if (criteria.hasKeyword() && criteria.getKeyword().length() > 255) {
            throw new IllegalArgumentException("کلمه کلیدی نمی‌تواند بیش از 255 کاراکتر باشد");
        }
        
        if (!criteria.isPriceRangeValid()) {
            throw new IllegalArgumentException("بازه قیمت نامعتبر است");
        }
    }
    
    private Pageable buildPageable(SearchCriteria criteria) {
        List<Sort.Order> orders = new ArrayList<>();
        
        // اضافه کردن مرتب‌سازی بر اساس نوع
        switch (criteria.getSortBy()) {
            case PRICE_ASC -> orders.add(Sort.Order.asc("price"));
            case PRICE_DESC -> orders.add(Sort.Order.desc("price"));
            case DATE_ASC -> orders.add(Sort.Order.asc("createdAt"));
            case DATE_DESC -> orders.add(Sort.Order.desc("createdAt"));
            case POPULARITY -> orders.add(Sort.Order.desc("viewCount"));
            default -> orders.add(Sort.Order.desc("createdAt"));
        }
        
        return PageRequest.of(criteria.getPage(), criteria.getSize(), Sort.by(orders));
    }
    
    private AdSearchResult convertToAdSearchResult(Ad ad) {
        return AdSearchResult.builder()
            .id(ad.getId())
            .title(ad.getTitle())
            .description(ad.getDescription())
            .price(ad.getPrice())
            .currency(ad.getCurrency())
            .status(ad.getStatus().name())
            .createdAt(ad.getCreatedAt())
            .updatedAt(ad.getUpdatedAt())
            .categoryId(ad.getCategory() != null ? ad.getCategory().getId() : null)
            .categoryName(ad.getCategory() != null ? ad.getCategory().getName() : null)
            .locationId(ad.getLocation() != null ? ad.getLocation().getId().longValue() : null)
            .locationName(ad.getLocation() != null ? ad.getLocation().getCity() : null)
            .userId(ad.getUser() != null ? ad.getUser().getId() : null)
            .userName(ad.getUser() != null ? ad.getUser().getUsername() : null)
            .viewCount(ad.getViewCount())
            .favoriteCount(ad.getFavoriteCount())
            .isFeatured(ad.getIsFeatured())
            .isUrgent(ad.getIsUrgent())
            .isVerified(ad.getUser() != null ? ad.getUser().getIsVerified() : false)
            .build();
    }
    
    private ProductSearchResult convertToProductSearchResult(Product product) {
        return ProductSearchResult.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .discountPrice(product.getDiscountPrice())
            .currency(product.getCurrency())
            .status(product.getStatus().name())
            .stockQuantity(product.getStockQuantity())
            .brand(product.getBrand())
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
            .salesCount(product.getSalesCount())
            .rating(product.getRating())
            .reviewCount(product.getReviewCount())
            .isFeatured(product.getIsFeatured())
            .isOnSale(product.getDiscountPrice() != null && 
                     product.getDiscountPrice().compareTo(product.getPrice()) < 0)
            .isInStock(product.getStockQuantity() != null && product.getStockQuantity() > 0)
            .isBestseller(product.getSalesCount() != null && product.getSalesCount() > 100)
            .build();
    }
}
```

**نکات مهم Service Layer:**

1. **Exception Handling**: همیشه try-catch استفاده کنید
2. **Logging**: لاگ‌های مفصل برای debug
3. **Validation**: اعتبارسنجی ورودی‌ها
4. **Caching**: کش کردن نتایج پرتکرار
5. **Performance Monitoring**: اندازه‌گیری زمان اجرا
6. **Analytics**: ثبت آمار استفاده

## Controller Layer

### SearchController

```java
@RestController
@RequestMapping("/api/search")
@Api(tags = "Search Controller", description = "API های جستجوی آگهی‌ها و محصولات")
@Slf4j
public class SearchController {
    
    private final SearchService searchService;
    
    @GetMapping("/ads")
    @ApiOperation(value = "جستجوی آگهی‌ها", 
                  notes = "جستجوی آگهی‌ها با امکان فیلتر و مرتب‌سازی")
    @ApiResponses({
        @ApiResponse(code = 200, message = "جستجو با موفقیت انجام شد"),
        @ApiResponse(code = 400, message = "پارامترهای ورودی نامعتبر"),
        @ApiResponse(code = 500, message = "خطای سرور")
    })
    public ResponseEntity<SearchResultPage<AdSearchResult>> searchAds(
            @Valid @ModelAttribute SearchCriteria criteria) {
        
        log.info("درخواست جستجوی آگهی‌ها: {}", criteria);
        
        try {
            SearchResultPage<AdSearchResult> results = searchService.searchAds(criteria);
            
            log.info("جستجوی آگهی‌ها انجام شد. {} نتیجه یافت شد", 
                results.getTotalCount());
            
            return ResponseEntity.ok(results);
            
        } catch (IllegalArgumentException e) {
            log.warn("پارامترهای نامعتبر: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("خطا در جستجوی آگهی‌ها: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/products")
    @ApiOperation(value = "جستجوی محصولات")
    public ResponseEntity<SearchResultPage<ProductSearchResult>> searchProducts(
            @Valid @ModelAttribute SearchCriteria criteria) {
        
        log.info("درخواست جستجوی محصولات: {}", criteria);
        
        try {
            SearchResultPage<ProductSearchResult> results = searchService.searchProducts(criteria);
            
            log.info("جستجوی محصولات انجام شد. {} نتیجه یافت شد", 
                results.getTotalCount());
            
            return ResponseEntity.ok(results);
            
        } catch (IllegalArgumentException e) {
            log.warn("پارامترهای نامعتبر: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("خطا در جستجوی محصولات: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/all")
    @ApiOperation(value = "جستجوی ترکیبی", 
                  notes = "جستجوی همزمان در آگهی‌ها و محصولات")
    public ResponseEntity<CombinedSearchResult> searchAll(
            @Valid @RequestBody SearchCriteria criteria) {
        
        log.info("درخواست جستجوی ترکیبی: {}", criteria);
        
        try {
            CombinedSearchResult results = searchService.searchAll(criteria);
            
            log.info("جستجوی ترکیبی انجام شد. {} آگهی و {} محصول یافت شد",
                results.getAds().getTotalCount(),
                results.getProducts().getTotalCount());
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            log.error("خطا در جستجوی ترکیبی: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/suggestions")
    @ApiOperation(value = "پیشنهادات جستجو")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") SearchType type) {
        
        try {
            List<String> suggestions = searchService.getSearchSuggestions(keyword, type);
            return ResponseEntity.ok(suggestions);
            
        } catch (Exception e) {
            log.error("خطا در دریافت پیشنهادات: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

**نکات Controller:**

1. **Validation**: استفاده از `@Valid` برای اعتبارسنجی
2. **Error Handling**: مدیریت استثناها
3. **Logging**: لاگ‌گذاری مناسب
4. **HTTP Status**: کدهای مناسب HTTP
5. **Swagger Documentation**: مستندسازی کامل API

## تنظیمات و کانفیگوریشن

### SearchConfig.java

```java
@Configuration
@EnableConfigurationProperties(SearchProperties.class)
@Slf4j
public class SearchConfig {
    
    @Bean
    @ConditionalOnMissingBean
    public SearchService searchService(
            AdSearchRepository adSearchRepository,
            ProductSearchRepository productSearchRepository,
            SearchAnalyticsService analyticsService) {
        
        log.info("ایجاد SearchService با پیکربندی پیش‌فرض");
        return new SearchServiceImpl(adSearchRepository, productSearchRepository, analyticsService);
    }
    
    @Bean
    public CacheManager searchCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("search-ads", "search-products");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
    
    @Bean
    @ConditionalOnProperty(name = "app.search.performance-monitoring", havingValue = "true")
    public SearchPerformanceMonitor searchPerformanceMonitor() {
        return new SearchPerformanceMonitor();
    }
}

@ConfigurationProperties(prefix = "app.search")
@Data
public class SearchProperties {
    private Integer defaultPageSize = 20;
    private Integer maxPageSize = 100;
    private Integer cacheTtl = 300;
    private Double fuzzyThreshold = 0.3;
    private Boolean enablePerformanceMonitoring = true;
    private String engine = "postgresql"; // postgresql یا elasticsearch
    
    // Elasticsearch settings
    private ElasticsearchProperties elasticsearch = new ElasticsearchProperties();
    
    @Data
    public static class ElasticsearchProperties {
        private String host = "localhost";
        private Integer port = 9200;
        private String username;
        private String password;
        private Boolean ssl = false;
    }
}
```

### Application Properties

```properties
# ===================================================================
# Search Configuration
# ===================================================================

# Basic Settings
app.search.default-page-size=20
app.search.max-page-size=100
app.search.cache-ttl=300
app.search.fuzzy-threshold=0.3
app.search.enable-performance-monitoring=true
app.search.engine=postgresql

# Elasticsearch Settings (for future migration)
app.search.elasticsearch.host=localhost
app.search.elasticsearch.port=9200
app.search.elasticsearch.ssl=false

# ===================================================================
# Database Configuration
# ===================================================================

# PostgreSQL Settings
spring.datasource.url=jdbc:postgresql://localhost:5432/iran_bazaar
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Settings
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.connection-timeout=20000

# ===================================================================
# Cache Configuration
# ===================================================================

spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=10000,expireAfterWrite=300s
spring.cache.cache-names=search-ads,search-products,search-suggestions

# ===================================================================
# Logging Configuration
# ===================================================================

logging.level.com.webrayan.commerce.modules.search=INFO
logging.level.org.springframework.cache=DEBUG
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN

# ===================================================================
# Performance Monitoring
# ===================================================================

management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.metrics.enabled=true
management.metrics.export.prometheus.enabled=true
```

## نحوه اضافه کردن ویژگی جدید

### 1. اضافه کردن فیلتر جدید

```java
// 1. اضافه کردن فیلد به SearchCriteria
@Data
public class SearchCriteria {
    // فیلدهای موجود...
    
    private String newFilter; // فیلتر جدید
    
    public boolean hasNewFilter() {
        return newFilter != null && !newFilter.trim().isEmpty();
    }
}

// 2. اضافه کردن به Repository Query
@Query(value = """
    SELECT a.* FROM ads a
    WHERE (:newFilter IS NULL OR a.new_field = :newFilter)
    -- سایر شرایط...
    """, nativeQuery = true)
Page<Ad> findByAdvancedSearch(
    // پارامترهای موجود...
    @Param("newFilter") String newFilter,
    Pageable pageable
);

// 3. اضافه کردن به Service
public SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria) {
    Page<Ad> ads = adSearchRepository.findByAdvancedSearch(
        // پارامترهای موجود...
        criteria.getNewFilter(),
        pageable
    );
    // باقی کد...
}
```

### 2. اضافه کردن نوع مرتب‌سازی جدید

```java
// 1. اضافه کردن به enum
public enum SortType {
    RELEVANCE,
    PRICE_ASC,
    PRICE_DESC,
    DATE_ASC,
    DATE_DESC,
    POPULARITY,
    NEW_SORT_TYPE // نوع جدید
}

// 2. اضافه کردن به buildPageable
private Pageable buildPageable(SearchCriteria criteria) {
    List<Sort.Order> orders = new ArrayList<>();
    
    switch (criteria.getSortBy()) {
        // موارد موجود...
        case NEW_SORT_TYPE -> orders.add(Sort.Order.desc("newField"));
    }
    
    return PageRequest.of(criteria.getPage(), criteria.getSize(), Sort.by(orders));
}
```

## تست و دیباگ

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {
    
    @Mock
    private AdSearchRepository adSearchRepository;
    
    @Mock
    private ProductSearchRepository productSearchRepository;
    
    @Mock
    private SearchAnalyticsService analyticsService;
    
    @InjectMocks
    private SearchServiceImpl searchService;
    
    @Test
    void searchAds_WithValidCriteria_ShouldReturnResults() {
        // Given
        SearchCriteria criteria = SearchCriteria.builder()
            .keyword("موبایل")
            .page(0)
            .size(20)
            .build();
            
        List<Ad> mockAds = Arrays.asList(createMockAd());
        Page<Ad> mockPage = new PageImpl<>(mockAds);
        
        when(adSearchRepository.findByAdvancedSearch(any(), any(), any(), 
            any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockPage);
        
        // When
        SearchResultPage<AdSearchResult> result = searchService.searchAds(criteria);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(1);
    }
    
    @Test
    void searchAds_WithInvalidPageSize_ShouldThrowException() {
        // Given
        SearchCriteria criteria = SearchCriteria.builder()
            .size(150) // بیش از حد مجاز
            .build();
        
        // When & Then
        assertThatThrownBy(() -> searchService.searchAds(criteria))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("حداکثر 100 آیتم در هر صفحه مجاز است");
    }
    
    private Ad createMockAd() {
        Ad ad = new Ad();
        ad.setId(1L);
        ad.setTitle("موبایل سامسونگ");
        ad.setPrice(new BigDecimal("5000000"));
        return ad;
    }
}
```

### Integration Tests

```java
@SpringBootTest
@Transactional
class SearchControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void searchAds_ShouldReturnValidResponse() {
        // Given
        String url = "/api/search/ads?keyword=موبایل&page=0&size=20";
        
        // When
        ResponseEntity<SearchResultPage> response = restTemplate.getForEntity(
            url, SearchResultPage.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getItems()).isNotNull();
    }
}
```

### Performance Tests

```java
@Component
public class SearchPerformanceTest {
    
    @Autowired
    private SearchService searchService;
    
    @EventListener
    public void measureSearchPerformance() {
        SearchCriteria criteria = SearchCriteria.builder()
            .keyword("تست")
            .size(100)
            .build();
            
        long startTime = System.currentTimeMillis();
        searchService.searchAds(criteria);
        long endTime = System.currentTimeMillis();
        
        long duration = endTime - startTime;
        if (duration > 3000) { // بیش از 3 ثانیه
            log.warn("جستجو کند است: {} میلی‌ثانیه", duration);
        }
    }
}
```

## بهترین شیوه‌ها (Best Practices)

### 1. کد نویسی

```java
// ✅ درست
@Service
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {
    
    private final AdSearchRepository adSearchRepository;
    
    // Constructor injection
    public SearchServiceImpl(AdSearchRepository adSearchRepository) {
        this.adSearchRepository = adSearchRepository;
    }
    
    @Override
    public SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria) {
        // Validation
        validateCriteria(criteria);
        
        // Business logic
        // ...
        
        // Return result
        return result;
    }
}

// ❌ غلط
@Service
public class SearchServiceImpl {
    @Autowired // Field injection - استفاده نکنید
    private AdSearchRepository repository;
    
    public SearchResultPage searchAds(SearchCriteria criteria) { // بدون generic type
        // بدون validation
        return repository.findAll(); // بدون تبدیل به DTO
    }
}
```

### 2. Exception Handling

```java
// ✅ درست
try {
    SearchResultPage<AdSearchResult> results = searchService.searchAds(criteria);
    return ResponseEntity.ok(results);
} catch (IllegalArgumentException e) {
    log.warn("پارامترهای نامعتبر: {}", e.getMessage());
    return ResponseEntity.badRequest()
        .body(new ErrorResponse("INVALID_PARAMS", e.getMessage()));
} catch (SearchException e) {
    log.error("خطا در جستجو: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("SEARCH_ERROR", "خطا در اجرای جستجو"));
}

// ❌ غلط
try {
    return searchService.searchAds(criteria);
} catch (Exception e) {
    return null; // هرگز null برنگردانید
}
```

### 3. Logging

```java
// ✅ درست
log.info("شروع جستجوی آگهی‌ها با معیارهای: keyword={}, categoryId={}", 
    criteria.getKeyword(), criteria.getCategoryId());

log.debug("جستجو انجام شد. {} نتیجه در {} میلی‌ثانیه یافت شد", 
    results.size(), searchTime);

// ❌ غلط
System.out.println("جستجو انجام شد"); // استفاده از System.out
log.info("جستجو با معیارهای: " + criteria); // String concatenation
```

### 4. Database Queries

```java
// ✅ درست
@Query(value = """
    SELECT a.* 
    FROM ads a
    WHERE (:keyword IS NULL OR 
           to_tsvector('persian', COALESCE(a.title, '')) 
           @@ plainto_tsquery('persian', :keyword))
      AND (:categoryId IS NULL OR a.category_id = :categoryId)
    ORDER BY a.created_at DESC
    """, nativeQuery = true)
Page<Ad> findByAdvancedSearch(@Param("keyword") String keyword,
                              @Param("categoryId") Long categoryId,
                              Pageable pageable);

// ❌ غلط
@Query("SELECT a FROM Ad a WHERE a.title LIKE %:keyword%") // SQL Injection risk
List<Ad> findByKeyword(@Param("keyword") String keyword);
```

## مسائل رایج و راه‌حل

### 1. کندی جستجو

**علت:** عدم وجود index مناسب

**راه‌حل:**
```sql
-- اضافه کردن GIN index برای Full-Text Search
CREATE INDEX CONCURRENTLY idx_ads_fts 
ON ads USING GIN(to_tsvector('persian', title || ' ' || description));

-- بررسی عملکرد query
EXPLAIN ANALYZE SELECT * FROM ads WHERE...;
```

### 2. خطای OutOfMemory

**علت:** بارگذاری تعداد زیاد رکورد

**راه‌حل:**
```java
// محدود کردن اندازه صفحه
@Value("${app.search.max-page-size:100}")
private Integer maxPageSize;

private void validateCriteria(SearchCriteria criteria) {
    if (criteria.getSize() > maxPageSize) {
        throw new IllegalArgumentException("حداکثر " + maxPageSize + " آیتم در هر صفحه مجاز است");
    }
}
```

### 3. خطای Cache

**علت:** کلید cache نامناسب

**راه‌حل:**
```java
// کلید منحصربه‌فرد برای cache
@Cacheable(value = "search-ads", 
           key = "#criteria.keyword + '_' + #criteria.categoryId + '_' + #criteria.page")
public SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria) {
    // ...
}
```

## مستندات API

تمام API های سیستم جستجو در Swagger مستند شده‌اند. برای دسترسی:

```
http://localhost:8080/swagger-ui.html
```

### نمونه درخواست‌ها

```bash
# جستجوی ساده
curl -X GET "http://localhost:8080/api/search/ads?keyword=موبایل&page=0&size=20"

# جستجوی پیشرفته
curl -X GET "http://localhost:8080/api/search/products?keyword=لپتاپ&categoryId=2&minPrice=5000000&maxPrice=25000000&sortBy=PRICE_ASC"

# جستجوی ترکیبی
curl -X POST "http://localhost:8080/api/search/all" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "سامسونگ",
    "categoryId": 1,
    "minPrice": 100000,
    "maxPrice": 15000000,
    "sortBy": "RELEVANCE",
    "page": 0,
    "size": 10
  }'
```

## نتیجه‌گیری

این مستند تمام جنبه‌های فنی سیستم جستجوی پرشیا بازار را پوشش می‌دهد. برای موفقیت در توسعه:

1. **کد تمیز** بنویسید
2. **تست کامل** انجام دهید  
3. **Performance** را مانیتور کنید
4. **Documentation** را به‌روز نگه دارید
5. **Best Practices** را رعایت کنید

برای سوالات بیشتر، با تیم معماری نرم‌افزار تماس بگیرید.
