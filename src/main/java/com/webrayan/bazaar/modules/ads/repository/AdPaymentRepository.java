package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.modules.ads.entity.AdPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdPaymentRepository extends JpaRepository<AdPayment, Long> {
    List<AdPayment> findByUserId(Long userId);
    List<AdPayment> findByAdId(Long adId);
    List<AdPayment> findByStatus(String status);
}
