package com.webrayan.commerce.modules.ads.controller;

import com.webrayan.commerce.core.common.entity.Location;
import com.webrayan.commerce.core.common.service.LocationService;
import com.webrayan.commerce.modules.ads.dto.AdRequestDto;
import com.webrayan.commerce.modules.ads.entity.Ad;
import com.webrayan.commerce.modules.ads.entity.AdCategory;
import com.webrayan.commerce.modules.ads.service.AdCategoryService;
import com.webrayan.commerce.modules.ads.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdPageController {

    private final AdService adService;
    private final AdCategoryService adCategoryService;
    private final LocationService locationService;

    /**
     * نمایش فرم ثبت آگهی
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            // دریافت لیست دسته‌بندی‌ها و مکان‌ها
            List<AdCategory> categories = adCategoryService.getAllCategories();
            List<Location> locations = locationService.getAllLocations();

            model.addAttribute("categories", categories);
            model.addAttribute("locations", locations);
            model.addAttribute("ad", new AdRequestDto());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Register a new ad");
            model.addAttribute("currentPath", "/ads/create");

            return "ads/create";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading page: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * ذخیره آگهی جدید
     */
    @PostMapping("/create")
    public String createAd(@ModelAttribute AdRequestDto adRequestDto,
                          @RequestParam(value = "images", required = false) org.springframework.web.multipart.MultipartFile[] images,
                          RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/auth/login";
            }

            // ایجاد آگهی از طریق DTO با تصاویر
            if (images != null && images.length > 0) {
                adService.createAd(adRequestDto, images);
            } else {
                adService.createAd(adRequestDto);
            }

            redirectAttributes.addFlashAttribute("successMessage", 
                "Your ad was successfully submitted and is awaiting approval.");
            
            return "redirect:/profile/ads";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error in registering Ad: " + e.getMessage());
            return "redirect:/ads/create";
        }
    }

    /**
     * نمایش فرم ویرایش آگهی
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }

            Ad ad = adService.getAdById(id);
            
            // بررسی مالکیت آگهی
            String username = auth.getName();
            if (!ad.getUser().getUsername().equals(username)) {
                return "redirect:/profile/ads?error=access_denied";
            }


            // دریافت لیست دسته‌بندی‌ها و مکان‌ها
            List<AdCategory> categories = adCategoryService.getAllCategories();
            List<Location> locations = locationService.getAllLocations();

            // تبدیل Ad به AdRequestDto
            AdRequestDto adRequestDto = new AdRequestDto();
            adRequestDto.setTitle(ad.getTitle());
            adRequestDto.setDescription(ad.getDescription());
            adRequestDto.setPrice(ad.getPrice());
            adRequestDto.setNegotiable(ad.getNegotiable());
            if (ad.getCategory() != null) {
                adRequestDto.setCategoryId(ad.getCategory().getId());
            }
            if (ad.getLocation() != null) {
                adRequestDto.setLocationId(ad.getLocation().getId().longValue());
            }

            // دریافت تصاویر آگهی
            java.util.List<com.webrayan.commerce.core.common.entity.Image> existingImages = new java.util.ArrayList<>();
            if (ad.getImages() != null && !ad.getImages().isEmpty()) {
                for (com.webrayan.commerce.modules.ads.entity.AdImage adImage : ad.getImages()) {
                    existingImages.add(adImage.getImage());
                }
            }

            model.addAttribute("categories", categories);
            model.addAttribute("locations", locations);
            model.addAttribute("ad", adRequestDto);
            model.addAttribute("adId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("existingImages", existingImages);
            model.addAttribute("pageTitle", "Edit Ad - " + ad.getTitle());

            return "ads/create";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading page: " + e.getMessage());
            return "redirect:/profile/ads";
        }
    }

    /**
     * ذخیره تغییرات آگهی
     */
    @PostMapping("/edit/{id}")
    public String updateAd(@PathVariable Long id, 
                          @ModelAttribute AdRequestDto adRequestDto,
                          @RequestParam(value = "images", required = false) org.springframework.web.multipart.MultipartFile[] images,
                          @RequestParam(value = "keepImages", required = false) java.util.List<Long> keepImages,
                          RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/auth/login";
            }

            Ad existingAd = adService.getAdById(id);
            
            // بررسی مالکیت آگهی
            String username = auth.getName();
            if (!existingAd.getUser().getUsername().equals(username)) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to edit this ad.");
                return "redirect:/profile/ads";
            }

            // ویرایش آگهی با تصاویر
            adService.updateAdWithImages(id, adRequestDto, images, keepImages);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Your ad has been successfully edited..");
            
            return "redirect:/profile/ads";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error editing adی: " + e.getMessage());
            return "redirect:/ads/edit/" + id;
        }
    }

    /**
     * نمایش آگهی در حالت فشرده
     */
    @GetMapping("/view-compact/{id}")
    public String viewCompactAd(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Ad ad = adService.getAdById(id);
            
            // افزایش تعداد بازدید
           // adService.incrementViewCount(id);
            
            model.addAttribute("ad", ad);
            model.addAttribute("pageTitle", ad.getTitle());
            
            // Check if user is authenticated
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated() 
                                    && !"anonymousUser".equals(authentication.getName());
            model.addAttribute("isAuthenticated", isAuthenticated);
            
            return "ads/view-compact";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "آگهی مورد نظر یافت نشد.");
            return "error/404";
        }
    }
}
