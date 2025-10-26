package com.webrayan.store.modules.ads.repository;

import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.entity.AdMessage;
import com.webrayan.store.modules.acl.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdMessageRepository extends JpaRepository<AdMessage, Long> {
    
    /**
     * دریافت پیام‌های دریافتی کاربر
     */
    @EntityGraph(attributePaths = {"sender", "ad", "ad.images", "ad.images.image"})
    Page<AdMessage> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);
    
    /**
     * دریافت پیام‌های ارسالی کاربر
     */
    @EntityGraph(attributePaths = {"receiver", "ad", "ad.images", "ad.images.image"})
    Page<AdMessage> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);
    
    /**
     * دریافت پیام‌های مربوط به یک آگهی
     */
    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<AdMessage> findByAdOrderByCreatedAtDesc(Ad ad);
    
    /**
     * شمارش پیام‌های خوانده نشده
     */
    long countByReceiverAndIsReadFalse(User receiver);
    
    /**
     * پیدا کردن پیام‌های خوانده نشده
     */
    @EntityGraph(attributePaths = {"sender", "ad"})
    List<AdMessage> findByReceiverAndIsReadFalseOrderByCreatedAtDesc(User receiver);
    
    /**
     * علامت‌گذاری پیام به عنوان خوانده شده
     */
    @Query("UPDATE AdMessage m SET m.isRead = true WHERE m.id = :messageId AND m.receiver = :receiver")
    void markAsRead(@Param("messageId") Long messageId, @Param("receiver") User receiver);
}
