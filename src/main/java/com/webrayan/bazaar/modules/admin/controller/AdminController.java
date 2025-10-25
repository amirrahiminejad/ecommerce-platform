package com.webrayan.bazaar.modules.admin.controller;

import com.webrayan.bazaar.modules.acl.service.UserService;
import com.webrayan.bazaar.modules.ads.entity.Ad;
import com.webrayan.bazaar.modules.ads.enums.AdStatus;
import com.webrayan.bazaar.modules.ads.repository.AdRepository;
import com.webrayan.bazaar.modules.ads.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller برای صفحات مدیریت (Admin)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdService adService;
    private final AdRepository adRepository;

    @Autowired
    public AdminController(UserService userService, AdService adService, AdRepository adRepository) {
        this.userService = userService;
        this.adService = adService;
        this.adRepository = adRepository;
    }

    /**
     * صفحه ورود ادمین
     */
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    /**
     * Debug endpoint برای بررسی آگهی‌ها
     */
    @GetMapping("/debug-ads")
    @ResponseBody
    public String debugAds() {
        StringBuilder result = new StringBuilder();
        
        long totalAds = adService.count();
        result.append("Total ads: ").append(totalAds).append("<br>");
        
        List<Ad> allAds = adRepository.findAll();
        result.append("All ads count from repository: ").append(allAds.size()).append("<br><br>");
        
        for (Ad ad : allAds) {
            result.append("ID: ").append(ad.getId())
                  .append(", Title: ").append(ad.getTitle())
                  .append(", Status: ").append(ad.getStatus())
                  .append(", Active: ").append(ad.getIsActive())
                  .append("<br>");
        }
        
        return result.toString();
    }

    /**
     * صفحه اصلی Dashboard
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        try {
            // آمار کلی
            long totalUsers = userService.count();
            long totalAds = adService.count();
            
            model.addAttribute("pageTitle", "داشبورد مدیریت");
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalProducts", 0L); // به محض اضافه شدن ProductService
            model.addAttribute("totalAds", totalAds);
            model.addAttribute("totalOrders", 0L); // به محض اضافه شدن OrderService
            
            // درصدها برای نمودارهای دایره‌ای
            model.addAttribute("approvedAdsPercentage", 75);
            model.addAttribute("activeUsersPercentage", 85);
            model.addAttribute("availableProductsPercentage", 90);
            model.addAttribute("completedOrdersPercentage", 65);
            model.addAttribute("userSatisfactionPercentage", 95);
            model.addAttribute("totalUploads", 1250);
            
            // فعالیت‌های اخیر (نمونه)
            List<Map<String, Object>> recentActivities = new ArrayList<>();
            Map<String, Object> activity1 = new HashMap<>();
            activity1.put("type", "user");
            activity1.put("icon", "icon-user-follow");
            activity1.put("message", "کاربر جدیدی ثبت نام کرد");
            activity1.put("createdAt", new java.util.Date());
            recentActivities.add(activity1);
            
            Map<String, Object> activity2 = new HashMap<>();
            activity2.put("type", "ad");
            activity2.put("icon", "icon-speech");
            activity2.put("message", "آگهی جدیدی منتشر شد");
            activity2.put("createdAt", new java.util.Date());
            recentActivities.add(activity2);
            
            model.addAttribute("recentActivities", recentActivities);
            
            // آگهی‌های در انتظار (نمونه)
            model.addAttribute("pendingAds", new ArrayList<>());
            
        } catch (Exception e) {
            // در صورت خطا، مقادیر پیش‌فرض
            model.addAttribute("pageTitle", "داشبورد مدیریت");
            model.addAttribute("totalUsers", 0L);
            model.addAttribute("totalProducts", 0L);
            model.addAttribute("totalAds", 0L);
            model.addAttribute("totalOrders", 0L);
            model.addAttribute("approvedAdsPercentage", 0);
            model.addAttribute("activeUsersPercentage", 0);
            model.addAttribute("availableProductsPercentage", 0);
            model.addAttribute("completedOrdersPercentage", 0);
            model.addAttribute("userSatisfactionPercentage", 0);
            model.addAttribute("totalUploads", 0);
            model.addAttribute("recentActivities", new ArrayList<>());
            model.addAttribute("pendingAds", new ArrayList<>());
        }
        
        return "admin/pages/dashboard";
    }

    /**
     * صفحه مدیریت آگهی‌ها
     */
    @GetMapping("/ads")
    public String ads(Model model,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "10") int size,
                      @RequestParam(defaultValue = "") String search,
                      @RequestParam(defaultValue = "") String status) {
        try {
            // آمار آگهی‌ها
            long totalAds = adService.count();
            long pendingAds = adService.countByStatus(AdStatus.PENDING);
            long approvedAds = adService.countByStatus(AdStatus.APPROVED);
            long rejectedAds = adService.countByStatus(AdStatus.REJECTED);
            
            model.addAttribute("pageTitle", "مدیریت آگهی‌ها");
            model.addAttribute("totalAds", totalAds);
            model.addAttribute("pendingAds", pendingAds);
            model.addAttribute("approvedAds", approvedAds);
            model.addAttribute("rejectedAds", rejectedAds);
            
            // Pagination setup
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Ad> adsPage;
            
            // Filter logic
            if (!search.trim().isEmpty() && !status.isEmpty()) {
                // جستجو در عنوان + فیلتر وضعیت
                AdStatus adStatus = AdStatus.valueOf(status.toUpperCase());
                adsPage = adRepository.findByTitleContainingAndStatus(search.trim(), adStatus, pageable);
            } else if (!search.trim().isEmpty()) {
                // فقط جستجو در عنوان
                adsPage = adRepository.findByTitleContaining(search.trim(), pageable);
            } else if (!status.isEmpty()) {
                // فقط فیلتر وضعیت
                AdStatus adStatus = AdStatus.valueOf(status.toUpperCase());
                adsPage = adRepository.findByStatus(adStatus, pageable);
            } else {
                // همه آگهی‌ها
                adsPage = adRepository.findAllWithPagination(pageable);
            }
            
            model.addAttribute("ads", adsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", adsPage.getTotalPages());
            model.addAttribute("totalElements", adsPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("search", search);
            model.addAttribute("selectedStatus", status);
            
        } catch (Exception e) {
            // در صورت خطا، مقادیر پیش‌فرض
            model.addAttribute("pageTitle", "مدیریت آگهی‌ها");
            model.addAttribute("totalAds", 0L);
            model.addAttribute("pendingAds", 0L);
            model.addAttribute("approvedAds", 0L);
            model.addAttribute("rejectedAds", 0L);
            model.addAttribute("ads", new ArrayList<Ad>());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
            model.addAttribute("size", 10);
            model.addAttribute("search", "");
            model.addAttribute("selectedStatus", "");
        }
        
        return "admin/pages/ads";
    }

    
    /**
     * صفحه جزئیات آگهی
     */
    @GetMapping("/ads/{id}")
    public String adDetail(@PathVariable Long id, Model model) {
        try {
            Ad ad = adService.getAdById(id);
            model.addAttribute("ad", ad);
            model.addAttribute("pageTitle", "جزئیات آگهی - " + ad.getTitle());
            
        } catch (Exception e) {
            model.addAttribute("pageTitle", "جزئیات آگهی");
            model.addAttribute("error", "آگهی مورد نظر یافت نشد");
            return "redirect:/admin/ads?error=notfound";
        }
        
        return "admin/pages/ad-detail";
    }
    
    /**
     * صفحه مدیریت آگهی
     */
    @GetMapping("/ads/{id}/edit")
    public String adManage(@PathVariable Long id, Model model) {
        try {
            Ad ad = adService.getAdById(id);
            model.addAttribute("ad", ad);
            model.addAttribute("pageTitle", "مدیریت آگهی - " + ad.getTitle());
            
        } catch (Exception e) {
            model.addAttribute("pageTitle", "مدیریت آگهی");
            model.addAttribute("error", "آگهی مورد نظر یافت نشد");
            return "redirect:/admin/ads?error=notfound";
        }
        
        return "admin/pages/ad-manage";
    }
    
    /**
     * تایید آگهی
     */
    @GetMapping("/ads/{id}/approve")
    public String approveAd(@PathVariable Long id) {
        try {
            adService.changeStatus(id, AdStatus.APPROVED, null);
            return "redirect:/admin/ads?success=approved";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/ads?error=approve_failed";
        }
    }
    
    /**
     * رد آگهی
     */
    @GetMapping("/ads/{id}/reject")
    public String rejectAd(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            adService.changeStatus(id, AdStatus.REJECTED, reason);
            return "redirect:/admin/ads?success=rejected";
        } catch (Exception e) {
            return "redirect:/admin/ads?error=reject_failed";
        }
    }
    
    /**
     * بروزرسانی وضعیت آگهی
     */
    @PostMapping("/ads/update")
    public String updateAdStatus(@RequestParam Long id,
                                @RequestParam AdStatus status,
                                @RequestParam(required = false) String rejectionReason,
                                @RequestParam(required = false, defaultValue = "false") Boolean isFeatured,
                                Model model) {
        try {
            // دریافت آگهی
            Ad ad = adService.getAdById(id);
            
            // تغییر وضعیت
            adService.changeStatus(id, status, rejectionReason);
            
            // تنظیم ویژه بودن (اگر نیاز باشد implementation کنید)
            // ad.setIsFeatured(isFeatured);
            
            return "redirect:/admin/ads?success=updated";
            
        } catch (Exception e) {
            model.addAttribute("error", "خطا در بروزرسانی آگهی: " + e.getMessage());
            return "redirect:/admin/ads/{id}/edit?error=update_failed";
        }
    }
}
