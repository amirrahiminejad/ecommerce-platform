package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.modules.ads.entity.Ad;
import com.webrayan.bazaar.modules.ads.entity.AdImage;
import com.webrayan.bazaar.core.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AdImageRepository extends JpaRepository<AdImage, Long> {
    // حذف ارتباط تصویر از آگهی
    @Transactional
    @Modifying
    @Query("DELETE FROM AdImage ai WHERE ai.ad = :ad AND ai.image = :image")
    void deleteByAdAndImage(Ad ad, Image image);

    // لیست تصاویر یک آگهی
    List<AdImage> findByAd(Ad ad);
}
