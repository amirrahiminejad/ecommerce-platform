package com.webrayan.store.modules.ads.controller;

import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;
import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.entity.AdFavorite;
import com.webrayan.store.modules.ads.service.AdService;
import com.webrayan.store.modules.ads.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService service;
    private final AdService adService;
    private final UserService userService;

    public FavoriteController(FavoriteService service, AdService adService, UserService userService) {
        this.service = service;
        this.adService = adService;
        this.userService = userService;
    }

    @GetMapping("/user/{userId}")
    public List<AdFavorite> getFavoritesByUser(@PathVariable Long userId) {
        return service.getFavoritesByUser(userId);
    }

    @PostMapping
    public AdFavorite addFavorite(@RequestBody AdFavorite adFavorite) {
        return service.addFavorite(adFavorite);
    }

    @DeleteMapping("/{id}")
    public void removeFavorite(@PathVariable Long id) {
        service.removeFavorite(id);
    }

    /**
     * اضافه کردن یا حذف آگهی از علاقه‌مندی‌ها
     */
    @PostMapping("/toggle/{adId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long adId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                response.put("success", false);
                response.put("message", "You must be logged in to add favorites");
                response.put("requireLogin", true);
                return ResponseEntity.ok(response);
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Ad ad = adService.getAdById(adId);

            // بررسی اینکه آیا کاربر نمی‌تواند آگهی خودش را به علاقه‌مندی اضافه کند
            if (ad.getUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "You cannot add your own ad to favorites");
                return ResponseEntity.ok(response);
            }

            // بررسی اینکه آیا قبلاً در علاقه‌مندی‌ها است یا نه
            boolean isFavorite = service.isAdInUserFavorites(user.getId(), adId);
            
            if (isFavorite) {
                // حذف از علاقه‌مندی‌ها
                service.removeFromFavorites(user.getId(), adId);
                response.put("success", true);
                response.put("action", "removed");
                response.put("message", "Removed from favorites");
                response.put("isFavorite", false);
            } else {
                // اضافه کردن به علاقه‌مندی‌ها
                AdFavorite adFavorite = new AdFavorite();
                adFavorite.setUser(user);
                adFavorite.setAd(ad);
                service.addFavorite(adFavorite);
                
                response.put("success", true);
                response.put("action", "added");
                response.put("message", "Added to favorites");
                response.put("isFavorite", true);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * بررسی اینکه آیا آگهی در علاقه‌مندی‌های کاربر است یا نه
     */
    @GetMapping("/check/{adId}")
    public ResponseEntity<Map<String, Object>> checkFavoriteStatus(@PathVariable Long adId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                response.put("isLoggedIn", false);
                response.put("isFavorite", false);
                return ResponseEntity.ok(response);
            }

            String username = auth.getName();
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            boolean isFavorite = service.isAdInUserFavorites(user.getId(), adId);
            
            response.put("isLoggedIn", true);
            response.put("isFavorite", isFavorite);
            response.put("success", true);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
