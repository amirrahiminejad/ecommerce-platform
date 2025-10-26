package com.webrayan.commerce.modules.common.controller;

import com.webrayan.commerce.core.util.AuthenticationUtil;
import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.service.UserService;
import com.webrayan.commerce.modules.ads.entity.Ad;
import com.webrayan.commerce.modules.ads.entity.AdFavorite;
import com.webrayan.commerce.modules.ads.entity.AdMessage;
import com.webrayan.commerce.modules.ads.enums.AdStatus;
import com.webrayan.commerce.modules.ads.service.AdService;
import com.webrayan.commerce.modules.ads.service.AdMessageService;
import com.webrayan.commerce.modules.ads.service.FavoriteService;
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
    private final AdService adService;
    private final AdMessageService adMessageService;
    private final FavoriteService favoriteService;
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
            long userAdsCount = adService.countByUser(user);
            long activeAdsCount = adService.countActiveAdsByUser(user);

            // آخرین آگهی‌ها برای نمایش در سایدبار
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Ad> latestAds;
            
            if (search != null && !search.trim().isEmpty()) {
                latestAds = adService.searchAds(search.trim(), pageable);
            } else {
                latestAds = adService.getApprovedAds(pageable);
            }

            model.addAttribute("user", user);
            model.addAttribute("userAdsCount", userAdsCount);
            model.addAttribute("activeAdsCount", activeAdsCount);
            model.addAttribute("latestAds", latestAds);
            model.addAttribute("search", search);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", latestAds.getTotalPages());
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
            Page<Ad> adsPage;

            if (!status.isEmpty()) {
                adsPage = adService.getAdsByUserAndStatus(user, status, pageable);
            } else {
                adsPage = adService.getAdsByUser(user, pageable);
            }

            // آمار آگهی‌های کاربر
            long totalUserAds = adService.countByUser(user);
            long approvedCount = adService.countByUserAndStatus(user, AdStatus.APPROVED);
            long pendingCount = adService.countByUserAndStatus(user, AdStatus.PENDING);
            long rejectedCount = adService.countByUserAndStatus(user, AdStatus.REJECTED);

            model.addAttribute("user", user);
            model.addAttribute("ads", adsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", adsPage.getTotalPages());
            model.addAttribute("totalElements", adsPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("pageTitle", "آگهی‌های من");
            model.addAttribute("currentPath", "/profile/ads");
            
            // آمار
            model.addAttribute("totalUserAds", totalUserAds);
            model.addAttribute("approvedCount", approvedCount);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("rejectedCount", rejectedCount);

            return "profile/my-ads";

        } catch (Exception e) {
            model.addAttribute("error", "خطا در نمایش آگهی‌ها");
            return "redirect:/profile";
        }
    }

    /**
     * حذف آگهی
     */
    @GetMapping("/ads/delete/{id}")
    public String deleteAd(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            String username = auth.getName();
            
            // بررسی مالکیت آگهی
            if (!adService.isAdOwner(id, username)) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this ad.");
                return "redirect:/profile/ads";
            }

            // حذف آگهی
            adService.deleteAd(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ad has been successfully deleted.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting ad: " + e.getMessage());
        }
        
        return "redirect:/profile/ads";
    }

    /**
     * آگهی‌های مورد علاقه
     */
    @GetMapping("/favorites")
    public String myFavorites(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            // دریافت آگهی‌های مورد علاقه
            java.util.List<AdFavorite> favoritesList = favoriteService.getFavoritesByUser(user.getId());
            
            // استخراج آگهی‌ها از لیست علاقه‌مندی‌ها
            java.util.List<Ad> favoriteAds = favoritesList.stream()
                    .map(AdFavorite::getAd)
                    .collect(java.util.stream.Collectors.toList());

            // Pagination manual (چون repository ما pagination ندارد)
            int start = page * size;
            int end = Math.min(start + size, favoriteAds.size());
            java.util.List<Ad> paginatedFavorites = favoriteAds.subList(start, end);
            
            int totalPages = (int) Math.ceil((double) favoriteAds.size() / size);

            model.addAttribute("user", user);
            model.addAttribute("favoriteAds", paginatedFavorites);
            model.addAttribute("favoritesList", favoritesList); // برای دسترسی به ID های AdFavorite
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", favoriteAds.size());
            model.addAttribute("size", size);
            model.addAttribute("pageTitle", "آگهی‌های مورد علاقه");
            model.addAttribute("currentPath", "/profile/favorites");

            return "profile/favorites";

        } catch (Exception e) {
            model.addAttribute("error", "خطا در نمایش آگهی‌های مورد علاقه");
            return "redirect:/profile";
        }
    }

    /**
     * حذف از آگهی‌های مورد علاقه
     */
    @GetMapping("/favorites/remove/{adId}")
    public String removeFavorite(@PathVariable Long adId, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            // بررسی مالکیت علاقه‌مندی
            AdFavorite favorite = favoriteService.findByUserIdAndAdId(user.getId(), adId);
            if (favorite == null || !favorite.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to remove this favorite.");
                return "redirect:/profile/favorites";
            }

            // حذف از علاقه‌مندی‌ها
            favoriteService.removeFromFavorites(user.getId(), adId);
            redirectAttributes.addFlashAttribute("successMessage", "Ad removed from favorites successfully.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing from favorites: " + e.getMessage());
        }
        
        return "redirect:/profile/favorites";
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
    
    /**
     * صفحه پیام‌های کاربر
     */
    @GetMapping("/messages")
    public String messages(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(defaultValue = "received") String type) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<AdMessage> messagesPage;
            
            // نوع پیام‌ها: received یا sent
            if ("sent".equals(type)) {
                messagesPage = adMessageService.getSentMessages(user, pageable);
            } else {
                messagesPage = adMessageService.getReceivedMessages(user, pageable);
            }
            
            // آمار پیام‌ها
            long unreadCount = adMessageService.getUnreadMessageCount(user);
            
            model.addAttribute("user", user);
            model.addAttribute("messages", messagesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", messagesPage.getTotalPages());
            model.addAttribute("totalElements", messagesPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("messageType", type);
            model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("pageTitle", "Messages");
            model.addAttribute("currentPath", "/profile/messages");

            return "profile/messages";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading messages");
            return "redirect:/profile";
        }
    }
}
