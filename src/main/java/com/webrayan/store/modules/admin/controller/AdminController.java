package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.modules.acl.service.UserService;
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

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * صفحه ورود ادمین
     */
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    /**
     * صفحه اصلی Dashboard
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        try {
            // آمار کلی
            long totalUsers = userService.count();

            model.addAttribute("pageTitle", "داشبورد مدیریت");
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalProducts", 0L); // به محض اضافه شدن ProductService
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

            model.addAttribute("pageTitle", "مدیریت آگهی‌ها");

            // Pagination setup
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            // Filter logic
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
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
            model.addAttribute("size", 10);
            model.addAttribute("search", "");
            model.addAttribute("selectedStatus", "");
        }
        
        return "admin/pages/ads";
    }

    

    

    

    

    

}
