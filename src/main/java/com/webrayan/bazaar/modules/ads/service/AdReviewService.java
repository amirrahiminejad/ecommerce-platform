package com.webrayan.bazaar.modules.ads.service;

import com.webrayan.bazaar.modules.ads.entity.AdReview;
import com.webrayan.bazaar.modules.ads.repository.AdReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdReviewService {

    private final AdReviewRepository adReviewRepository;

    public AdReviewService(AdReviewRepository adReviewRepository) {
        this.adReviewRepository = adReviewRepository;
    }

    public List<AdReview> getAllReviews() {
        return adReviewRepository.findAll();
    }

    public List<AdReview> getReviewsByAdId(Long adId) {
        return adReviewRepository.findByAdId(adId);
    }

    public List<AdReview> getReviewsByUserId(Long userId) {
        return adReviewRepository.findByUserId(userId);
    }

    public AdReview saveReview(AdReview adReview) {
        return adReviewRepository.save(adReview);
    }

    public void deleteReview(Long id) {
        adReviewRepository.deleteById(id);
    }
}
