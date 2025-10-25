package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.modules.ads.entity.AdStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdStatusHistoryRepository extends JpaRepository<AdStatusHistory, Long> {
    List<AdStatusHistory> findByAdId(Long adId);
}
