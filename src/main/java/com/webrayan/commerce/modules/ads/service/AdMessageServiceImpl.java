package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.dto.AdMessageDto;
import com.webrayan.commerce.modules.ads.entity.Ad;
import com.webrayan.commerce.modules.ads.entity.AdMessage;
import com.webrayan.commerce.modules.ads.repository.AdMessageRepository;
import com.webrayan.commerce.modules.ads.repository.AdRepository;
import com.webrayan.commerce.modules.acl.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdMessageServiceImpl implements AdMessageService {
    
    private final AdMessageRepository adMessageRepository;
    private final AdRepository adRepository;
    
    @Override
    public AdMessage sendMessage(AdMessageDto adMessageDto, User sender) {
        // پیدا کردن آگهی
        Ad ad = adRepository.findById(adMessageDto.getAdId())
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        
        // بررسی اینکه کاربر به خودش پیام نفرستد
        if (sender != null && ad.getUser().getId().equals(sender.getId())) {
            throw new RuntimeException("You cannot send message to your own ad");
        }
        
        AdMessage message = new AdMessage();
        message.setContent(adMessageDto.getContent());
        message.setAd(ad);
        message.setReceiver(ad.getUser()); // صاحب آگهی
        message.setSender(sender); // ممکن است null باشد برای کاربران غیر احراز هویت شده
        
        // اگر کاربر احراز هویت نشده باشد، نام و ایمیل را ذخیره کن
        if (sender == null) {
            message.setSenderName(adMessageDto.getSenderName());
            message.setSenderEmail(adMessageDto.getSenderEmail());
        }
        
        message.setCreatedAt(new Date());
        message.setIsRead(false);
        
        return adMessageRepository.save(message);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AdMessage> getReceivedMessages(User receiver, Pageable pageable) {
        return adMessageRepository.findByReceiverOrderByCreatedAtDesc(receiver, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AdMessage> getSentMessages(User sender, Pageable pageable) {
        return adMessageRepository.findBySenderOrderByCreatedAtDesc(sender, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AdMessage> getMessagesByAd(Ad ad) {
        return adMessageRepository.findByAdOrderByCreatedAtDesc(ad);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(User receiver) {
        return adMessageRepository.countByReceiverAndIsReadFalse(receiver);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AdMessage> getUnreadMessages(User receiver) {
        return adMessageRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(receiver);
    }
    
    @Override
    public void markAsRead(Long messageId, User receiver) {
        adMessageRepository.markAsRead(messageId, receiver);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AdMessage getMessageById(Long messageId) {
        return adMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }
}
