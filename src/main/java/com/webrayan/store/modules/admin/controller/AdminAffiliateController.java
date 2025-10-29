package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/affiliates")
@RequiredArgsConstructor
public class AdminAffiliateController {

    private final UserService userService;

    /**
     * لیست بازاریابها
     */
    @GetMapping
    public String listAffiliates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String status,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            
            // فیلتر کردن کاربران با role بازاریاب
            Page<User> affiliates = userService.findAffiliateUsers(pageable, search, status);
            
            model.addAttribute("affiliates", affiliates);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", affiliates.getTotalPages());
            model.addAttribute("totalElements", affiliates.getTotalElements());
            model.addAttribute("searchTerm", search);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("userStatuses", User.UserStatus.values());
            
            return "admin/pages/affiliates";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "خطا در بارگذاری لیست بازاریابها: " + e.getMessage());
            return "admin/pages/affiliates";
        }
    }

    /**
     * جزئیات بازاریاب
     */
    @GetMapping("/{id}")
    public String viewAffiliate(@PathVariable Long id, Model model) {
        try {
            User affiliate = userService.findById(id);
            
            // بررسی اینکه کاربر بازاریاب هست
            boolean isAffiliate = affiliate.getRoles().stream()
                    .anyMatch(role -> role.getRoleName() == Role.RoleName.AFFILIATE);
            
            if (!isAffiliate) {
                model.addAttribute("errorMessage", "کاربر مورد نظر بازاریاب نیست");
                return "redirect:/admin/affiliates";
            }
            
            model.addAttribute("affiliate", affiliate);
            
            // آمارهای بازاریاب (برای آینده)
            // model.addAttribute("totalCommissions", affiliateService.getTotalCommissions(id));
            // model.addAttribute("activeReferrals", affiliateService.getActiveReferrals(id));
            
            return "admin/pages/affiliate-detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "خطا در نمایش جزئیات بازاریاب: " + e.getMessage());
            return "redirect:/admin/affiliates";
        }
    }

    /**
     * تغییر وضعیت بازاریاب
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleAffiliateStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User affiliate = userService.findById(id);
            
            // بررسی اینکه کاربر بازاریاب هست
            boolean isAffiliate = affiliate.getRoles().stream()
                    .anyMatch(role -> role.getRoleName() == Role.RoleName.AFFILIATE);
            
            if (!isAffiliate) {
                redirectAttributes.addFlashAttribute("errorMessage", "کاربر مورد نظر بازاریاب نیست");
                return "redirect:/admin/affiliates";
            }
            
            // تغییر وضعیت
            User.UserStatus newStatus = affiliate.getStatus() == User.UserStatus.ACTIVE 
                    ? User.UserStatus.INACTIVE 
                    : User.UserStatus.ACTIVE;
            
            affiliate.setStatus(newStatus);
            userService.save(affiliate);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "وضعیت بازاریاب با موفقیت به " + newStatus.getPersianName() + " تغییر یافت");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در تغییر وضعیت بازاریاب: " + e.getMessage());
        }
        
        return "redirect:/admin/affiliates";
    }

    /**
     * حذف بازاریاب (تغییر وضعیت به DELETED)
     */
    @PostMapping("/delete/{id}")
    public String deleteAffiliate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User affiliate = userService.findById(id);
            
            // بررسی اینکه کاربر بازاریاب هست
            boolean isAffiliate = affiliate.getRoles().stream()
                    .anyMatch(role -> role.getRoleName() == Role.RoleName.AFFILIATE);
            
            if (!isAffiliate) {
                redirectAttributes.addFlashAttribute("errorMessage", "کاربر مورد نظر بازاریاب نیست");
                return "redirect:/admin/affiliates";
            }
            
            affiliate.setStatus(User.UserStatus.DELETED);
            userService.save(affiliate);
            
            redirectAttributes.addFlashAttribute("successMessage", "بازاریاب با موفقیت حذف شد");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در حذف بازاریاب: " + e.getMessage());
        }
        
        return "redirect:/admin/affiliates";
    }
}
