package com.webrayan.store.modules.common.controller;


import com.webrayan.store.modules.acl.service.UserService;
import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.entity.AdCategory;
import com.webrayan.store.modules.ads.enums.AdStatus;
import com.webrayan.store.modules.ads.service.AdCategoryService;
import com.webrayan.store.modules.ads.service.AdService;
import com.webrayan.store.core.common.entity.Location;
import com.webrayan.store.core.common.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AdService adService;
    private final AdCategoryService adCategoryService;
    private final LocationService locationService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "12") int size,
                      @RequestParam(defaultValue = "") String search,
                      @RequestParam(required = false) Long category,
                      @RequestParam(required = false) Long location,
                      @RequestParam(defaultValue = "createdAt,desc") String sort,
                      java.security.Principal principal) {

        try {
            // Check if user is authenticated
            boolean isAuthenticated = principal != null;
            String username = isAuthenticated && principal != null ? principal.getName() : null;
            
            model.addAttribute("isAuthenticated", isAuthenticated);
//            User user = userService.getUserByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

            model.addAttribute("user", username);
            // آمار کلی برای Hero Section
            long totalAds = adService.countByStatus(AdStatus.APPROVED);
            long totalUsers = userService.count();

            // Sort configuration
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0];
            String sortDir = sortParams.length > 1 ? sortParams[1] : "desc";
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // دسته‌بندی‌ها و مکان‌ها برای sidebar
            List<AdCategory> categories = adCategoryService.getAllCategories();
            List<Location> locations = locationService.getAllLocations();

            // آگهی‌ها با فیلتر
            Page<Ad> adsPage;
            AdCategory selectedCategory = null;
            Location selectedLocation = null;

            // Set selected location first if provided
            if (location != null) {
                selectedLocation = locationService.getLocationById(location);
            }

            if (category != null && location != null) {
                // Both category and location selected
                selectedCategory = adCategoryService.getCategoryById(category).orElse(null);
                if (!search.trim().isEmpty()) {
                    // Category + Location + Search - use general search for now
                    adsPage = adService.searchAds(search.trim(), pageable);
                } else {
                    // Category + Location only
                    adsPage = adService.getAdsByCategoryAndLocation(selectedCategory, selectedLocation, pageable);
                }
            } else if (category != null) {
                // Only category selected
                selectedCategory = adCategoryService.getCategoryById(category).orElse(null);
                if (!search.trim().isEmpty()) {
                    adsPage = adService.searchAdsByCategoryAndTitle(selectedCategory, search.trim(), pageable);
                } else {
                    adsPage = adService.getAdsByCategory(selectedCategory, pageable);
                }
            } else if (location != null) {
                // Only location selected
                if (!search.trim().isEmpty()) {
                    adsPage = adService.searchAdsByLocationAndTitle(selectedLocation, search.trim(), pageable);
                } else {
                    adsPage = adService.getAdsByLocation(selectedLocation, pageable);
                }
            } else if (!search.trim().isEmpty()) {
                // Only search
                adsPage = adService.searchAds(search.trim(), pageable);
            } else {
                // No filters
                adsPage = adService.getApprovedAds(pageable);
            }

            // Model attributes
            model.addAttribute("ads", adsPage.getContent());
            model.addAttribute("categories", categories);
            model.addAttribute("locations", locations);
            model.addAttribute("selectedCategory", selectedCategory);
            model.addAttribute("selectedLocation", selectedLocation);
            model.addAttribute("search", search);
            model.addAttribute("sort", sort);

            // Pagination
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", adsPage.getTotalPages());
            model.addAttribute("totalElements", adsPage.getTotalElements());
            model.addAttribute("size", size);

            // Statistics
            model.addAttribute("totalAds", totalAds);
            model.addAttribute("totalUsers", totalUsers);

        } catch (Exception e) {
            e.printStackTrace();
            // Error handling - default values
            model.addAttribute("ads", List.of());
            model.addAttribute("categories", List.of());
            model.addAttribute("locations", List.of());
            model.addAttribute("selectedCategory", null);
            model.addAttribute("selectedLocation", null);
            model.addAttribute("search", "");
            model.addAttribute("sort", "createdAt,desc");
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
            model.addAttribute("size", 12);
            model.addAttribute("totalAds", 0L);
            model.addAttribute("totalUsers", 0L);
        }

        return "home";
    }

    @GetMapping("/ads/{id}")
    public String viewAd(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Ad ad = adService.getAdById(id);
//            if (ad.getStatus() != AdStatus.APPROVED) {
//                return "error/404";
//            }
            
            model.addAttribute("ad", ad);
            model.addAttribute("pageTitle", ad.getTitle());
            
            // Check if user is authenticated
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated() 
                                    && !"anonymousUser".equals(authentication.getName());
            model.addAttribute("isAuthenticated", isAuthenticated);
            
            // Related ads (آگهی‌های مشابه)
            List<Ad> similarAds = adService.getRelatedAds(ad, 6);
            model.addAttribute("similarAds", similarAds);
            
            // افزایش تعداد بازدید
            //adService.incrementViewCount(id);
            
        } catch (Exception e) {
            return "error/404";
        }
        
        return "ads/view";
    }
}
