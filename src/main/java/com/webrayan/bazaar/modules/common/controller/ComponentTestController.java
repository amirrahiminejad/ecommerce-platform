package com.webrayan.bazaar.modules.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * کنترلر صفحه تست کامپوننت‌ها
 * این کنترلر برای نمایش و تست کامپوننت‌های قابل استفاده مجدد طراحی شده
 */
@Controller
@RequestMapping("/test")
public class ComponentTestController {

    /**
     * نمایش صفحه تست کامپوننت‌ها
     * 
     * @param model مدل Spring MVC
     * @return نام template برای نمایش
     */
    @GetMapping("/components")
    public String showComponentsTestPage(Model model) {
        // اضافه کردن اطلاع��ت مفید برای صفحه تست
        model.addAttribute("pageTitle", "تست کامپوننت‌ها");
        model.addAttribute("totalComponents", 1); // تعداد کامپوننت‌های آماده
        model.addAttribute("inDevelopment", 5);   // تعداد کامپوننت‌های در حال توسعه
        
        return "test/components";
    }
    
    /**
     * صفحه اصلی تست (در صورت نیاز به صفحه index برای تست‌ها)
     */
    @GetMapping
    public String testIndex() {
        return "redirect:/test/components";
    }
    
    /**
     * API endpoint برای تست‌های AJAX (در صورت نیاز)
     */
    @GetMapping("/api/status")
    public String getTestStatus() {
        // می‌تواند برای تست‌های AJAX استفاده شود
        return "redirect:/test/components";
    }
}
