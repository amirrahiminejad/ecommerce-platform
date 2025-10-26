package com.webrayan.store.modules.ads.controller;

import com.webrayan.store.modules.ads.dto.AdRequestDto;
import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.enums.AdStatus;
import com.webrayan.store.modules.ads.service.AdServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ads Management", description = "API های مدیریت آگهی‌ها")
@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private AdServiceImpl adService;

    @Operation(
        summary = "ایجاد آگهی جدید",
        description = "ایجاد آگهی جدید توسط کاربران احراز هویت شده",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "آگهی با موفقیت ایجاد شد",
            content = @Content(schema = @Schema(implementation = Ad.class))
        ),
        @ApiResponse(responseCode = "401", description = "احراز هویت نشده"),
        @ApiResponse(responseCode = "403", description = "دسترسی مجاز نیست")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'SYSTEM_ADMIN')")
    public Ad createAd(
        @Parameter(description = "اطلاعات آگهی جدید", required = true)
        @Valid @RequestBody AdRequestDto ad) {
        return adService.createAd(ad);
    }

    @Operation(
        summary = "ویرایش آگهی",
        description = "ویرایش آگهی توسط صاحب آگهی یا ادمین",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or (isAuthenticated() and @adService.isAdOwner(#id, authentication.name))")
    public Ad updateAd(
        @Parameter(description = "شناسه آگهی", required = true) @PathVariable Long id,
        @Parameter(description = "اطلاعات به‌روزرسانی آگهی", required = true) @Valid @RequestBody AdRequestDto updatedAd) {
        return adService.updateAd(id, updatedAd);
    }

    @Operation(
        summary = "تغییر وضعیت آگهی",
        description = "تغییر وضعیت آگهی توسط ادمین یا کاربران دارای مجوز مدیریت",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('AD:MODERATE')")
    public Ad changeAdStatus(
        @Parameter(description = "شناسه آگهی", required = true) @PathVariable Long id,
        @Parameter(description = "وضعیت جدید", required = true) @RequestParam AdStatus status,
        @Parameter(description = "دلیل تغییر وضعیت") @RequestParam(required = false) String reason) {
        return adService.changeStatus(id, status, reason);
    }

    @Operation(
        summary = "دریافت آگهی با شناسه",
        description = "دریافت جزئیات کامل یک آگهی"
    )
    @ApiResponse(
        responseCode = "200",
        description = "اطلاعات آگهی",
        content = @Content(schema = @Schema(implementation = Ad.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAdById(
        @Parameter(description = "شناسه آگهی", required = true) @PathVariable Long id) {
        Ad ad = adService.getAdById(id);
        return ResponseEntity.ok(ad);
    }

    @Operation(
        summary = "جستجوی آگهی‌ها بر اساس وضعیت",
        description = "دریافت لیست آگهی‌ها بر اساس وضعیت مشخص"
    )
    @GetMapping
    public List<Ad> getAdsByStatus(
        @Parameter(description = "وضعیت آگهی", required = true) @RequestParam AdStatus status) {
        return adService.getAdsByStatus(status);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or #userId == authentication.principal.id")
    public List<Ad> getAdsByUser(@PathVariable Long userId) {
        return adService.getAdsByUser(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or (isAuthenticated() and @adService.isAdOwner(#id, authentication.name))")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        adService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "افزایش تعداد بازدید آگهی",
        description = "افزایش تعداد بازدید آگهی هنگام مشاهده"
    )
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        try {
            adService.incrementViewCount(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
