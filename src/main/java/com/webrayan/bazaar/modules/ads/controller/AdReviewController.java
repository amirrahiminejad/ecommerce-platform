package com.webrayan.bazaar.modules.ads.controller;

import com.webrayan.bazaar.modules.ads.entity.AdReview;
import com.webrayan.bazaar.modules.ads.service.AdReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class AdReviewController {

    private final AdReviewService adReviewService;

    public AdReviewController(AdReviewService adReviewService) {
        this.adReviewService = adReviewService;
    }

    @GetMapping
    public ResponseEntity<List<AdReview>> getAllReviews() {
        return ResponseEntity.ok(adReviewService.getAllReviews());
    }

    @GetMapping("/ad/{adId}")
    public ResponseEntity<List<AdReview>> getReviewsByAdId(@PathVariable Long adId) {
        return ResponseEntity.ok(adReviewService.getReviewsByAdId(adId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AdReview>> getReviewsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adReviewService.getReviewsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<AdReview> createReview(@RequestBody AdReview adReview) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adReviewService.saveReview(adReview));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        adReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
