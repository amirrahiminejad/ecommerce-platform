package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.modules.ads.entity.AdFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<AdFavorite, Long> {
    
    @Query("SELECT f FROM AdFavorite f " +
           "JOIN FETCH f.ad a " +
           "LEFT JOIN FETCH a.images img " +
           "LEFT JOIN FETCH img.image " +
           "WHERE f.user.id = :userId")
    List<AdFavorite> findByUserId(@Param("userId") Long userId);
    
    boolean existsByUserIdAndAdId(Long userId, Long adId);
    AdFavorite findByUserIdAndAdId(Long userId, Long adId);
    void deleteByUserIdAndAdId(Long userId, Long adId);
}
