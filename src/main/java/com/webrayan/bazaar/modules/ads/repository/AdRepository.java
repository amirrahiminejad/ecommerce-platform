package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.modules.ads.entity.Ad;
import com.webrayan.bazaar.modules.ads.entity.AdCategory;
import com.webrayan.bazaar.modules.ads.enums.AdStatus;
import com.webrayan.bazaar.core.common.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByStatus(AdStatus status);
    List<Ad> findByUser_Id(Long userId);
    List<Ad> findByCategory_Id(Long categoryId);
    
    /**
     * شمارش آگهی‌ها بر اساس وضعیت
     */
    Long countByStatus(AdStatus status);
    
    // Pagination methods for admin panel
    
    /**
     * دریافت آگهی‌ها با pagination
     */
    @Query("SELECT a FROM Ad a ORDER BY a.createdAt DESC")
    Page<Ad> findAllWithPagination(Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس وضعیت با pagination
     */
    Page<Ad> findByStatus(AdStatus status, Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس وضعیت فعال/غیرفعال با pagination
     */
    Page<Ad> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * جستجو در عنوان آگهی با pagination
     */
    @Query("SELECT a FROM Ad a WHERE a.title LIKE %:title% ORDER BY a.createdAt DESC")
    Page<Ad> findByTitleContaining(@Param("title") String title, Pageable pageable);
    
    /**
     * دریافت آگهی با تصاویر (eager loading)
     */
    @Query("SELECT DISTINCT a FROM Ad a LEFT JOIN FETCH a.images WHERE a.id = :id")
    Ad findByIdWithImages(@Param("id") Long id);
    
    /**
     * جستجو در عنوان آگهی و وضعیت با pagination
     */
    @Query("SELECT a FROM Ad a WHERE a.title LIKE %:title% AND a.status = :status ORDER BY a.createdAt DESC")
    Page<Ad> findByTitleContainingAndStatus(@Param("title") String title, @Param("status") AdStatus status, Pageable pageable);
    
    // New methods for home page
    
    /**
     * دریافت آگهی‌های تایید شده با ترتیب تاریخ
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusOrderByCreatedAtDesc(AdStatus status, Pageable pageable);
    
    /**
     * دریافت آگهی‌های تایید شده با images (eager loading)
     */
    @Query("SELECT DISTINCT a FROM Ad a LEFT JOIN FETCH a.images WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Ad> findByStatusWithImages(@Param("status") AdStatus status, Pageable pageable);
    
    /**
     * جستجو در آگهی‌های تایید شده
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
        AdStatus status, String title, Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس دسته‌بندی
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusAndCategoryOrderByCreatedAtDesc(
        AdStatus status, AdCategory category, Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس مکان
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusAndLocationOrderByCreatedAtDesc(
        AdStatus status, Location location, Pageable pageable);
    
    /**
     * دریافت آگہی‌ها بر اساس دسته‌بندی و مکان
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusAndCategoryAndLocationOrderByCreatedAtDesc(
        AdStatus status, AdCategory category, Location location, Pageable pageable);
    
    /**
     * جستجو در آگهی‌ها بر اساس دسته‌بندی و عنوان
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusAndCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
        AdStatus status, AdCategory category, String title, Pageable pageable);
    
    /**
     * جستجو در آگهی‌ها بر اساس مکان و عنوان
     */
    @EntityGraph(attributePaths = {"images", "images.image", "location", "category", "user"})
    Page<Ad> findByStatusAndLocationAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
        AdStatus status, Location location, String title, Pageable pageable);
    
    /**
     * دریافت آگهی‌های مرتبط (همان دسته‌بندی)
     */
    @EntityGraph(attributePaths = {"images", "images.image"})
    Page<Ad> findByStatusAndCategoryAndIdNotOrderByCreatedAtDesc(
        AdStatus status, AdCategory category, Long excludeId, Pageable pageable);
    
    /**
     * دریافت آگهی‌های مرتبط (بدون دسته‌بندی خاص)
     */
    @EntityGraph(attributePaths = {"images", "images.image"})
    Page<Ad> findByStatusAndIdNotOrderByCreatedAtDesc(
        AdStatus status, Long excludeId, Pageable pageable);
    
    // Profile related methods
    
    /**
     * شمارش آگهی‌های کاربر
     */
    Long countByUser(com.webrayan.bazaar.modules.acl.entity.User user);
    
    /**
     * شمارش آگهی‌های فعال کاربر
     */
    Long countByUserAndStatusAndIsActive(
        com.webrayan.bazaar.modules.acl.entity.User user, 
        AdStatus status, 
        Boolean isActive);
    
    /**
     * دریافت آگهی‌های کاربر با تصاویر
     */
    @Query("SELECT DISTINCT a FROM Ad a LEFT JOIN FETCH a.images img LEFT JOIN FETCH img.image WHERE a.user = :user ORDER BY a.createdAt DESC")
    Page<Ad> findByUserOrderByCreatedAtDesc(
        @Param("user") com.webrayan.bazaar.modules.acl.entity.User user, 
        Pageable pageable);
    
    /**
     * دریافت آگهی‌های کاربر بر اساس وضعیت با تصاویر
     */
    @Query("SELECT DISTINCT a FROM Ad a LEFT JOIN FETCH a.images img LEFT JOIN FETCH img.image WHERE a.user = :user AND a.status = :status ORDER BY a.createdAt DESC")
    Page<Ad> findByUserAndStatusOrderByCreatedAtDesc(
        @Param("user") com.webrayan.bazaar.modules.acl.entity.User user, 
        @Param("status") AdStatus status, 
        Pageable pageable);

    /**
     * شمارش آگهی‌های کاربر بر اساس وضعیت
     */
    long countByUserAndStatus(
        com.webrayan.bazaar.modules.acl.entity.User user, 
        AdStatus status);
}
