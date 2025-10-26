package com.webrayan.store.modules.common.controller;


import com.webrayan.store.modules.acl.service.UserService;
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
            long totalUsers = userService.count();

            // Sort configuration
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0];
            String sortDir = sortParams.length > 1 ? sortParams[1] : "desc";
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // دسته‌بندی‌ها و مکان‌ها برای sidebar
            List<Location> locations = locationService.getAllLocations();

            // آگهی‌ها با فیلتر
            Location selectedLocation = null;

            // Set selected location first if provided
            if (location != null) {
                selectedLocation = locationService.getLocationById(location);
            }



            // Model attributes
            model.addAttribute("locations", locations);
            model.addAttribute("selectedLocation", selectedLocation);
            model.addAttribute("search", search);
            model.addAttribute("sort", sort);

            // Pagination
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);

            // Statistics
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

}
