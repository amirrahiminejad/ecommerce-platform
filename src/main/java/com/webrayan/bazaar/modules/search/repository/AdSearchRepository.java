package com.webrayan.bazaar.modules.search.repository;

import com.webrayan.bazaar.modules.ads.entity.Ad;
import com.webrayan.bazaar.modules.search.dto.AdSearchResult;
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
 * Repository برای جستجوی پیشرفته آگهی‌ها
 */
@Repository
public interface AdSearchRepository extends JpaRepository<Ad, Long> {

    /**
     * جستجوی تمام متن با PostgreSQL
     */
    @Query(value = """
        SELECT DISTINCT a.*, 
               COALESCE(ts_rank(to_tsvector('english', a.title || ' ' || COALESCE(a.description, '')), 
                                plainto_tsquery('english', :keyword)), 0) as relevance_score
        FROM ads a 
        LEFT JOIN ad_categories ac ON a.category_id = ac.id
        LEFT JOIN locations l ON a.location_id = l.id
        LEFT JOIN users u ON a.user_id = u.id
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
             to_tsvector('english', a.title || ' ' || COALESCE(a.description, '')) @@ plainto_tsquery('english', :keyword))
            AND (:categoryId IS NULL OR a.category_id = :categoryId)
            AND (:locationId IS NULL OR a.location_id = :locationId)
            AND (:minPrice IS NULL OR a.price >= :minPrice)
            AND (:maxPrice IS NULL OR a.price <= :maxPrice)
            AND (:status IS NULL OR a.status = :status)
            AND (:activeOnly IS FALSE OR a.is_active = true)
            AND (:featuredOnly IS FALSE OR a.is_featured = true)
            AND (:daysBack IS NULL OR a.created_at >= :dateFrom)
        ORDER BY 
            CASE 
                WHEN :sortBy = 'RELEVANCE' THEN relevance_score
                WHEN :sortBy = 'PRICE_ASC' THEN a.price
                WHEN :sortBy = 'PRICE_DESC' THEN -a.price
                WHEN :sortBy = 'DATE_ASC' THEN EXTRACT(EPOCH FROM a.created_at)
                WHEN :sortBy = 'DATE_DESC' THEN -EXTRACT(EPOCH FROM a.created_at)
                WHEN :sortBy = 'POPULARITY' THEN -a.views_count
                ELSE relevance_score
            END DESC
        """, 
        countQuery = """
        SELECT COUNT(DISTINCT a.id)
        FROM ads a 
        LEFT JOIN ad_categories ac ON a.category_id = ac.id
        LEFT JOIN locations l ON a.location_id = l.id
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
             to_tsvector('english', a.title || ' ' || COALESCE(a.description, '')) @@ plainto_tsquery('english', :keyword))
            AND (:categoryId IS NULL OR a.category_id = :categoryId)
            AND (:locationId IS NULL OR a.location_id = :locationId)
            AND (:minPrice IS NULL OR a.price >= :minPrice)
            AND (:maxPrice IS NULL OR a.price <= :maxPrice)
            AND (:status IS NULL OR a.status = :status)
            AND (:activeOnly IS FALSE OR a.is_active = true)
            AND (:featuredOnly IS FALSE OR a.is_featured = true)
            AND (:daysBack IS NULL OR a.created_at >= :dateFrom)
        """,
        nativeQuery = true)
    Page<Ad> searchAds(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("locationId") Long locationId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("status") String status,
        @Param("activeOnly") Boolean activeOnly,
        @Param("featuredOnly") Boolean featuredOnly,
        @Param("daysBack") Integer daysBack,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("sortBy") String sortBy,
        Pageable pageable
    );

    /**
     * جستجوی فازی برای پیشنهاد املایی
     */
    @Query(value = """
        SELECT DISTINCT a.title
        FROM ads a
        WHERE 
            similarity(a.title, :keyword) > 0.3
            AND a.is_active = true
            AND a.status = 'APPROVED'
        ORDER BY similarity(a.title, :keyword) DESC
        LIMIT 10
        """, nativeQuery = true)
    List<String> findSimilarTitles(@Param("keyword") String keyword);

    /**
     * جستجوی برچسب‌ها
     */
    @Query(value = """
        SELECT DISTINCT a.*
        FROM ads a
        JOIN ad_tags at ON a.id = at.ad_id
        JOIN tags t ON at.tag_id = t.id
        WHERE 
            t.name IN (:tags)
            AND a.is_active = true
            AND a.status = 'APPROVED'
        """, nativeQuery = true)
    List<Ad> findByTags(@Param("tags") List<String> tags);

    /**
     * آگهی‌های مشابه بر اساس دسته‌بندی و قیمت
     */
    @Query(value = """
        SELECT a.*
        FROM ads a
        WHERE 
            a.category_id = :categoryId
            AND a.id != :excludeId
            AND a.price BETWEEN :minPrice AND :maxPrice
            AND a.is_active = true
            AND a.status = 'APPROVED'
        ORDER BY ABS(a.price - :targetPrice)
        LIMIT 10
        """, nativeQuery = true)
    List<Ad> findSimilarAds(
        @Param("categoryId") Long categoryId,
        @Param("excludeId") Long excludeId,
        @Param("targetPrice") BigDecimal targetPrice,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );

    /**
     * جستجوی آگهی‌های محبوب
     */
    @Query(value = """
        SELECT a.*
        FROM ads a
        WHERE 
            a.is_active = true
            AND a.status = 'APPROVED'
            AND a.created_at >= :dateFrom
        ORDER BY a.views_count DESC, a.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Ad> findPopularAds(@Param("dateFrom") LocalDateTime dateFrom, @Param("limit") Integer limit);

    /**
     * جستجوی آگهی‌های اخیر
     */
    @Query(value = """
        SELECT a.*
        FROM ads a
        WHERE 
            a.is_active = true
            AND a.status = 'APPROVED'
            AND (:categoryId IS NULL OR a.category_id = :categoryId)
        ORDER BY a.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Ad> findRecentAds(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * آمار جستجو برای داشبورد
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_count,
            COUNT(CASE WHEN a.status = 'APPROVED' THEN 1 END) as approved_count,
            COUNT(CASE WHEN a.is_featured = true THEN 1 END) as featured_count,
            COUNT(CASE WHEN a.created_at >= :dateFrom THEN 1 END) as recent_count
        FROM ads a
        WHERE a.is_active = true
        """, nativeQuery = true)
    Object[] getSearchStatistics(@Param("dateFrom") LocalDateTime dateFrom);

    /**
     * محبوب‌ترین دسته‌بندی‌ها
     */
    @Query(value = """
        SELECT 
            ac.name as category_name,
            COUNT(a.id) as ad_count,
            SUM(a.views_count) as total_views
        FROM ads a
        JOIN ad_categories ac ON a.category_id = ac.id
        WHERE 
            a.is_active = true
            AND a.status = 'APPROVED'
            AND a.created_at >= :dateFrom
        GROUP BY ac.id, ac.name
        ORDER BY ad_count DESC, total_views DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getPopularCategories(@Param("dateFrom") LocalDateTime dateFrom, @Param("limit") Integer limit);

    /**
     * جستجوی متن کامل فقط در عنوان
     */
    @Query(value = """
        SELECT DISTINCT a.*
        FROM ads a
        WHERE 
            to_tsvector('english', a.title) @@ plainto_tsquery('english', :keyword)
            AND a.is_active = true
            AND a.status = 'APPROVED'
        ORDER BY ts_rank(to_tsvector('english', a.title), plainto_tsquery('english', :keyword)) DESC
        """, nativeQuery = true)
    List<Ad> searchInTitle(@Param("keyword") String keyword, Pageable pageable);

    /**
     * جستجوی متن کامل فقط در توضیحات
     */
    @Query(value = """
        SELECT DISTINCT a.*
        FROM ads a
        WHERE 
            to_tsvector('english', COALESCE(a.description, '')) @@ plainto_tsquery('english', :keyword)
            AND a.is_active = true
            AND a.status = 'APPROVED'
        ORDER BY ts_rank(to_tsvector('english', COALESCE(a.description, '')), plainto_tsquery('english', :keyword)) DESC
        """, nativeQuery = true)
    List<Ad> searchInDescription(@Param("keyword") String keyword, Pageable pageable);
}
