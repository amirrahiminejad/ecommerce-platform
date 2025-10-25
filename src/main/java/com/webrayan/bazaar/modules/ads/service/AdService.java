package com.webrayan.bazaar.modules.ads.service;

import com.webrayan.bazaar.modules.ads.dto.AdRequestDto;
import com.webrayan.bazaar.modules.ads.entity.Ad;
import com.webrayan.bazaar.modules.ads.entity.AdCategory;
import com.webrayan.bazaar.modules.ads.enums.AdStatus;
import com.webrayan.bazaar.core.common.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdService {
    Ad createAd(AdRequestDto adRequest);
    Ad createAd(AdRequestDto adRequest, org.springframework.web.multipart.MultipartFile[] images);
    Ad updateAd(Long id, AdRequestDto adRequest);
    Ad updateAdWithImages(Long id, AdRequestDto adRequest, org.springframework.web.multipart.MultipartFile[] images, java.util.List<Long> keepImages);
    Ad changeStatus(Long id, AdStatus status, String reason);
    Ad getAdById(Long id);
    List<Ad> getAdsByStatus(AdStatus status);
    List<Ad> getAdsByUser(Long userId);
    void deleteAd(Long id);
    
    /**
     * شمارش کل آگهی‌ها
     */
    long count();
    
    /**
     * شمارش آگهی‌ها بر اساس وضعیت
     */
    long countByStatus(AdStatus status);
    
    // New methods for home page
    
    /**
     * دریافت آگهی‌های تایید شده با pagination
     */
    Page<Ad> getApprovedAds(Pageable pageable);
    
    /**
     * جستجوی آگهی‌ها
     */
    Page<Ad> searchAds(String searchTerm, Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس دسته‌بندی
     */
    Page<Ad> getAdsByCategory(AdCategory category, Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس مکان
     */
    Page<Ad> getAdsByLocation(Location location, Pageable pageable);
    
    /**
     * دریافت آگهی‌ها بر اساس دسته‌بندی و مکان
     */
    Page<Ad> getAdsByCategoryAndLocation(AdCategory category, Location location, Pageable pageable);
    
    /**
     * جستجوی آگهی‌ها بر اساس دسته‌بندی و عنوان
     */
    Page<Ad> searchAdsByCategoryAndTitle(AdCategory category, String searchTerm, Pageable pageable);
    
    /**
     * جستجوی آگهی‌ها بر اساس مکان و عنوان
     */
    Page<Ad> searchAdsByLocationAndTitle(Location location, String searchTerm, Pageable pageable);
    
    /**
     * دریافت آگهی‌های مرتبط
     */
    List<Ad> getRelatedAds(Ad ad, int limit);
    
    // Profile related methods
    
    /**
     * شمارش آگهی‌های کاربر
     */
    long countByUser(com.webrayan.bazaar.modules.acl.entity.User user);
    
    /**
     * شمارش آگهی‌های فعال کاربر
     */
    long countActiveAdsByUser(com.webrayan.bazaar.modules.acl.entity.User user);
    
    /**
     * دریافت آگهی‌های کاربر
     */
    Page<Ad> getAdsByUser(com.webrayan.bazaar.modules.acl.entity.User user, Pageable pageable);
    
    /**
     * دریافت آگهی‌های کاربر بر اساس وضعیت
     */
    Page<Ad> getAdsByUserAndStatus(com.webrayan.bazaar.modules.acl.entity.User user, String status, Pageable pageable);

    /**
     * شمارش آگهی‌های کاربر بر اساس وضعیت
     */
    long countByUserAndStatus(com.webrayan.bazaar.modules.acl.entity.User user, com.webrayan.bazaar.modules.ads.enums.AdStatus status);

    /**
     * افزایش تعداد بازدید آگهی
     */
    void incrementViewCount(Long adId);
    
    /**
     * بررسی اینکه آیا کاربر مالک آگهی است یا نه
     */
    boolean isAdOwner(Long adId, String username);
}
