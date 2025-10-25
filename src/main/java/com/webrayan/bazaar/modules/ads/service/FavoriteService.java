package com.webrayan.bazaar.modules.ads.service;

import com.webrayan.bazaar.modules.ads.entity.AdFavorite;
import com.webrayan.bazaar.modules.ads.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository repository;

    public FavoriteService(FavoriteRepository repository) {
        this.repository = repository;
    }

    public List<AdFavorite> getFavoritesByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public AdFavorite addFavorite(AdFavorite adFavorite) {
        if (adFavorite.getUser() == null || adFavorite.getAd() == null) {
            throw new IllegalArgumentException("User and Ad must be specified.");
        }
        if (repository.existsByUserIdAndAdId(adFavorite.getUser().getId(), adFavorite.getAd().getId())) {
            throw new IllegalArgumentException("This ad is already in user's favorites.");
        }
        return repository.save(adFavorite);
    }

    public void removeFavorite(Long favoriteId) {
        repository.deleteById(favoriteId);
    }

    /**
     * بررسی اینکه آیا آگهی در علاقه‌مندی‌های کاربر است یا نه
     */
    public boolean isAdInUserFavorites(Long userId, Long adId) {
        return repository.existsByUserIdAndAdId(userId, adId);
    }

    /**
     * حذف آگهی از علاقه‌مندی‌های کاربر
     */
    public void removeFromFavorites(Long userId, Long adId) {
        AdFavorite favorite = repository.findByUserIdAndAdId(userId, adId);
        if (favorite != null) {
            repository.delete(favorite);
        }
    }

    /**
     * دریافت AdFavorite بر اساس userId و adId
     */
    public AdFavorite findByUserIdAndAdId(Long userId, Long adId) {
        return repository.findByUserIdAndAdId(userId, adId);
    }
}
