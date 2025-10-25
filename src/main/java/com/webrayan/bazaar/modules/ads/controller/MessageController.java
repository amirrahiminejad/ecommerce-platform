package com.webrayan.bazaar.modules.ads.controller;

import com.webrayan.bazaar.modules.ads.dto.AdMessageDto;
import com.webrayan.bazaar.modules.ads.entity.AdMessage;
import com.webrayan.bazaar.modules.ads.service.AdMessageService;
import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.modules.acl.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final AdMessageService adMessageService;
    private final UserService userService;
    
    /**
     * ارسال پیام برای آگهی
     */
    @PostMapping("/send")
    public String sendMessage(@ModelAttribute AdMessageDto adMessageDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        try {
            // بررسی احراز هویت کاربر
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
            User sender = null;
            
            if (isAuthenticated) {
                sender = userService.getUserByUsername(auth.getName()).orElse(null);
                // برای کاربران احراز هویت شده، نام و ایمیل اجباری نیست
                adMessageDto.setSenderName(null);
                adMessageDto.setSenderEmail(null);
            } else {
                // برای کاربران غیر احراز هویت شده، بررسی فیلدهای اجباری
                if (adMessageDto.getSenderName() == null || adMessageDto.getSenderName().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Name is required for anonymous users.");
                    return "redirect:/ads/" + adMessageDto.getAdId();
                }
                if (adMessageDto.getSenderEmail() == null || adMessageDto.getSenderEmail().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Email is required for anonymous users.");
                    return "redirect:/ads/" + adMessageDto.getAdId();
                }
                if (!adMessageDto.getSenderEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                    redirectAttributes.addFlashAttribute("error", "Please enter a valid email address.");
                    return "redirect:/ads/" + adMessageDto.getAdId();
                }
            }
            
            // بررسی محتوای پیام
            if (adMessageDto.getContent() == null || adMessageDto.getContent().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Message content is required.");
                return "redirect:/ads/" + adMessageDto.getAdId();
            }
            if (adMessageDto.getContent().length() < 10 || adMessageDto.getContent().length() > 1000) {
                redirectAttributes.addFlashAttribute("error", "Message must be between 10 and 1000 characters.");
                return "redirect:/ads/" + adMessageDto.getAdId();
            }
            
            // ارسال پیام
            adMessageService.sendMessage(adMessageDto, sender);
            
            redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");
            return "redirect:/ads/" + adMessageDto.getAdId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
            return "redirect:/ads/" + adMessageDto.getAdId();
        }
    }
    
    /**
     * علامت‌گذاری پیام به عنوان خوانده شده
     */
    @PostMapping("/mark-read/{messageId}")
    public String markAsRead(@PathVariable Long messageId,
                            RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/auth/login";
            }
            
            User user = userService.getUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            adMessageService.markAsRead(messageId, user);
            
            redirectAttributes.addFlashAttribute("success", "Message marked as read");
            return "redirect:/profile/messages";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to mark message as read: " + e.getMessage());
            return "redirect:/profile/messages";
        }
    }
}
