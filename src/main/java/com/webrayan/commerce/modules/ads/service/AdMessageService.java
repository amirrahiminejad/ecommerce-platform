package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.dto.AdMessageDto;
import com.webrayan.commerce.modules.ads.entity.Ad;
import com.webrayan.commerce.modules.ads.entity.AdMessage;
import com.webrayan.commerce.modules.acl.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdMessageService {
    
    /**
     * ارسال پیام برای آگهی
     * @param adMessageDto اطلاعات پیام
     * @param sender کاربر ارسال کننده (null برای کاربران غیر احراز هویت شده)
     * @return پیام ایجاد شده
     */
    AdMessage sendMessage(AdMessageDto adMessageDto, User sender);
    
    /**
     * دریافت پیام‌های دریافتی کاربر
     */
    Page<AdMessage> getReceivedMessages(User receiver, Pageable pageable);
    
    /**
     * دریافت پیام‌های ارسالی کاربر
     */
    Page<AdMessage> getSentMessages(User sender, Pageable pageable);
    
    /**
     * دریافت پیام‌های مربوط به یک آگهی
     */
    List<AdMessage> getMessagesByAd(Ad ad);
    
    /**
     * شمارش پیام‌های خوانده نشده
     */
    long getUnreadMessageCount(User receiver);
    
    /**
     * دریافت پیام‌های خوانده نشده
     */
    List<AdMessage> getUnreadMessages(User receiver);
    
    /**
     * علامت‌گذاری پیام به عنوان خوانده شده
     */
    void markAsRead(Long messageId, User receiver);
    
    /**
     * دریافت پیام بر اساس ID
     */
    AdMessage getMessageById(Long messageId);
}
