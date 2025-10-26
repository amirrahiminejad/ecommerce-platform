package com.webrayan.store.modules.common.controller;

import com.webrayan.store.core.util.AuthenticationUtil;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * صفحه پروفایل کاربر
     */
    @GetMapping
    public String profile(Model model, 
                         @RequestParam(defaultValue = "") String search,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "9") int size) {
        try {
            User user = authenticationUtil.getCurrentUser()
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            // آمار کاربر

            // آخرین آگهی‌ها برای نمایش در سایدبار
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());



            model.addAttribute("user", user);
            model.addAttribute("search", search);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageTitle", "پروفایل من");
            model.addAttribute("isOAuth2User", authenticationUtil.isOAuth2User());
            model.addAttribute("currentPath", "/profile");

            return "profile/profile";

        } catch (Exception e) {
            model.addAttribute("error", "خطا در نمایش پروفایل");
            return "redirect:/";
        }
    }

    /**
     * آگهی‌های کاربر
     */
    @GetMapping("/ads")
    public String myAds(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "") String status) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());




            model.addAttribute("user", user);
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("pageTitle", "آگهی‌های من");
            model.addAttribute("currentPath", "/profile/ads");
            
               return "profile/my-ads";

        } catch (Exception e) {
            model.addAttribute("error", "خطا در نمایش آگهی‌ها");
            return "redirect:/profile";
        }
    }

     /**
     * ویرایش پروفایل
     */
    @GetMapping("/edit")
    public String editProfile(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "ویرایش پروفایل");
            model.addAttribute("currentPath", "/profile/edit");

            return "profile/edit";

        } catch (Exception e) {
            model.addAttribute("error", "خطا در نمایش صفحه ویرایش");
            return "redirect:/profile";
        }
    }

    /**
     * به‌روزرسانی پروفایل
     */
    @PostMapping("/update")
    public String updateProfile(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam String phoneNumber,
                               @RequestParam(required = false) String linkdin,
                               @RequestParam(required = false) String facebook,
                               @RequestParam(required = false) String instagram,
                               @RequestParam(required = false) String whatsapp,
                               @RequestParam(required = false) String telegram,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            // به‌روزرسانی اطلاعات اصلی
            userService.updateUser(user.getId(), firstName, lastName, phoneNumber, email);
            
            // به‌روزرسانی لینک‌های شبکه‌های اجتماعی
            userService.updateSocialMediaLinks(user.getId(), linkdin, facebook, instagram, whatsapp, telegram);

            redirectAttributes.addFlashAttribute("success", "پروفایل با موفقیت به‌روزرسانی شد");
            return "redirect:/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "خطا در به‌روزرسانی پروفایل: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }
    
    /**
     * صفحه تغییر رمز عبور
     */
    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            model.addAttribute("user", user);
            model.addAttribute("currentPath", "/profile/change-password");
            return "profile/change-password";

        } catch (Exception e) {
            model.addAttribute("error", "خطا در نمایش صفحه تغییر رمز عبور");
            return "redirect:/profile";
        }
    }
    
}
