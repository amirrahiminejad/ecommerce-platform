package com.webrayan.commerce.modules.search.repository;

import com.webrayan.commerce.modules.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository برای جستجوی پیشرفته محصولات
 */
@Repository
public interface ProductSearchRepository extends JpaRepository<Product, Long> {

    /**
     * جستجوی تمام متن محصولات با PostgreSQL
     */
    @Query(value = """
        SELECT DISTINCT p.*, 
               COALESCE(ts_rank(to_tsvector('english', p.name || ' ' || COALESCE(p.short_description, '') || ' ' || COALESCE(p.description, '')), 
                                plainto_tsquery('english', :keyword)), 0) as relevance_score
        FROM catalog_products p 
        LEFT JOIN catalog_categories cc ON p.category_id = cc.id
        LEFT JOIN users u ON p.seller_id = u.id
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
             to_tsvector('english', p.name || ' ' || COALESCE(p.short_description, '') || ' ' || COALESCE(p.description, '')) @@ plainto_tsquery('english', :keyword))
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:status IS NULL OR p.status = :status)
            AND (:activeOnly IS FALSE OR p.is_active = true)
            AND (:featuredOnly IS FALSE OR p.is_featured = true)
            AND (:inStockOnly IS FALSE OR p.stock_quantity > 0)
            AND (:digitalOnly IS NULL OR p.is_digital = :digitalOnly)
            AND (:daysBack IS NULL OR p.created_at >= :dateFrom)
        ORDER BY 
            CASE 
                WHEN :sortBy = 'RELEVANCE' THEN relevance_score
                WHEN :sortBy = 'PRICE_ASC' THEN p.price
                WHEN :sortBy = 'PRICE_DESC' THEN -p.price
                WHEN :sortBy = 'DATE_ASC' THEN EXTRACT(EPOCH FROM p.created_at)
                WHEN :sortBy = 'DATE_DESC' THEN -EXTRACT(EPOCH FROM p.created_at)
                WHEN :sortBy = 'POPULARITY' THEN -p.views_count
                WHEN :sortBy = 'SALES' THEN -p.sales_count
                WHEN :sortBy = 'NAME_ASC' THEN p.name
                WHEN :sortBy = 'NAME_DESC' THEN p.name
                ELSE relevance_score
            END DESC
        """, 
        countQuery = """
        SELECT COUNT(DISTINCT p.id)
        FROM catalog_products p 
        LEFT JOIN catalog_categories cc ON p.category_id = cc.id
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
             to_tsvector('english', p.name || ' ' || COALESCE(p.short_description, '') || ' ' || COALESCE(p.description, '')) @@ plainto_tsquery('english', :keyword))
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:status IS NULL OR p.status = :status)
            AND (:activeOnly IS FALSE OR p.is_active = true)
            AND (:featuredOnly IS FALSE OR p.is_featured = true)
            AND (:inStockOnly IS FALSE OR p.stock_quantity > 0)
            AND (:digitalOnly IS NULL OR p.is_digital = :digitalOnly)
            AND (:daysBack IS NULL OR p.created_at >= :dateFrom)
        """,
        nativeQuery = true)
    Page<Product> searchProducts(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("status") String status,
        @Param("activeOnly") Boolean activeOnly,
        @Param("featuredOnly") Boolean featuredOnly,
        @Param("inStockOnly") Boolean inStockOnly,
        @Param("digitalOnly") Boolean digitalOnly,
        @Param("daysBack") Integer daysBack,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("sortBy") String sortBy,
        Pageable pageable
    );

    /**
     * جستجوی فازی برای پیشنهاد املایی محصولات
     */
    @Query(value = """
        SELECT DISTINCT p.name
        FROM catalog_products p
        WHERE 
            similarity(p.name, :keyword) > 0.3
            AND p.is_active = true
            AND p.status = 'PUBLISHED'
        ORDER BY similarity(p.name, :keyword) DESC
        LIMIT 10
        """, nativeQuery = true)
    List<String> findSimilarProductNames(@Param("keyword") String keyword);

    /**
     * محصولات مشابه بر اساس دسته‌بندی و قیمت
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            p.category_id = :categoryId
            AND p.id != :excludeId
            AND p.price BETWEEN :minPrice AND :maxPrice
            AND p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.stock_quantity > 0
        ORDER BY ABS(p.price - :targetPrice), p.sales_count DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Product> findSimilarProducts(
        @Param("categoryId") Long categoryId,
        @Param("excludeId") Long excludeId,
        @Param("targetPrice") BigDecimal targetPrice,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );

    /**
     * محصولات پرفروش
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.stock_quantity > 0
            AND p.created_at >= :dateFrom
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY p.sales_count DESC, p.views_count DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findBestSellingProducts(
        @Param("categoryId") Long categoryId, 
        @Param("dateFrom") LocalDateTime dateFrom, 
        @Param("limit") Integer limit
    );

    /**
     * محصولات ویژه
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.is_featured = true
            AND p.stock_quantity > 0
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY p.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findFeaturedProducts(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * محصولات با تخفیف
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.stock_quantity > 0
            AND p.discount_price IS NOT NULL
            AND p.discount_price < p.price
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY ((p.price - p.discount_price) / p.price) DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findDiscountedProducts(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * محصولات اخیر
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.stock_quantity > 0
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY p.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findRecentProducts(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * جستجو در SKU محصولات
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            LOWER(p.sku) LIKE LOWER(CONCAT('%', :sku, '%'))
            AND p.is_active = true
            AND p.status = 'PUBLISHED'
        ORDER BY p.name
        """, nativeQuery = true)
    List<Product> findBySku(@Param("sku") String sku, Pageable pageable);

    /**
     * محصولات موجود در محدوده قیمتی
     */
    @Query(value = """
        SELECT p.*
        FROM catalog_products p
        WHERE 
            p.price BETWEEN :minPrice AND :maxPrice
            AND p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.stock_quantity > 0
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY p.price ASC
        """, nativeQuery = true)
    List<Product> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );

    /**
     * آمار محصولات برای داشبورد
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_count,
            COUNT(CASE WHEN p.status = 'PUBLISHED' THEN 1 END) as published_count,
            COUNT(CASE WHEN p.is_featured = true THEN 1 END) as featured_count,
            COUNT(CASE WHEN p.stock_quantity > 0 THEN 1 END) as in_stock_count,
            COUNT(CASE WHEN p.discount_price IS NOT NULL THEN 1 END) as discounted_count,
            COUNT(CASE WHEN p.created_at >= :dateFrom THEN 1 END) as recent_count
        FROM catalog_products p
        WHERE p.is_active = true
        """, nativeQuery = true)
    Object[] getProductStatistics(@Param("dateFrom") LocalDateTime dateFrom);

    /**
     * محبوب‌ترین دسته‌بندی‌های محصولات
     */
    @Query(value = """
        SELECT 
            cc.name as category_name,
            COUNT(p.id) as product_count,
            SUM(p.views_count) as total_views,
            SUM(p.sales_count) as total_sales
        FROM catalog_products p
        JOIN catalog_categories cc ON p.category_id = cc.id
        WHERE 
            p.is_active = true
            AND p.status = 'PUBLISHED'
            AND p.created_at >= :dateFrom
        GROUP BY cc.id, cc.name
        ORDER BY product_count DESC, total_sales DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getPopularProductCategories(@Param("dateFrom") LocalDateTime dateFrom, @Param("limit") Integer limit);

    /**
     * جستجوی متن کامل فقط در نام محصول
     */
    @Query(value = """
        SELECT DISTINCT p.*
        FROM catalog_products p
        WHERE 
            to_tsvector('english', p.name) @@ plainto_tsquery('english', :keyword)
            AND p.is_active = true
            AND p.status = 'PUBLISHED'
        ORDER BY ts_rank(to_tsvector('english', p.name), plainto_tsquery('english', :keyword)) DESC
        """, nativeQuery = true)
    List<Product> searchInProductName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * جستجوی advance با فیلتر برندها و ویژگی‌ها
     */
    @Query(value = """
        SELECT DISTINCT p.*
        FROM catalog_products p
        LEFT JOIN product_attribute_values pav ON p.id = pav.product_id
        LEFT JOIN catalog_attributes ca ON pav.attribute_id = ca.id
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
             to_tsvector('english', p.name || ' ' || COALESCE(p.description, '')) @@ plainto_tsquery('english', :keyword))
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
            AND p.is_active = true
            AND p.status = 'PUBLISHED'
            AND (
                :brandsListEmpty = true OR 
                (ca.name = 'brand' AND pav.value IN (:brands))
            )
        ORDER BY p.sales_count DESC, p.views_count DESC
        """, nativeQuery = true)
    List<Product> searchWithBrands(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("brands") List<String> brands,
        @Param("brandsListEmpty") Boolean brandsListEmpty,
        Pageable pageable
    );
}
