package com.webrayan.commerce.modules.ads.repository;

import com.webrayan.commerce.modules.ads.entity.AdReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdReviewRepository extends JpaRepository<AdReview, Long> {
    List<AdReview> findByAdId(Long adId);
    List<AdReview> findByUserId(Long userId);
}
