package com.webrayan.commerce.modules.acl.controller;

import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for password reset pages
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetPageController {
    
    private final PasswordResetService passwordResetService;
    
    /**
     * Show forgot password page
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        return "auth/forgot-password";
    }
    
    /**
     * Process forgot password form
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                       RedirectAttributes redirectAttributes) {
        try {
            String result = passwordResetService.initiatePasswordReset(email.trim().toLowerCase());
            
            redirectAttributes.addFlashAttribute("successMessage", result);
            return "redirect:/auth/forgot-password";
            
        } catch (Exception e) {
            log.error("خطا در پردازش درخواست فراموشی رمز عبور: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "خطایی رخ داد. لطفاً بعداً تلاش کنید.");
            return "redirect:/auth/forgot-password";
        }
    }
    
    /**
     * Show reset password page
     */
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam(required = false) String token,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        
        if (token == null || token.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "لینک بازیابی نامعتبر است.");
            return "redirect:/auth/forgot-password";
        }
        
        // Validate token
        if (!passwordResetService.validateResetToken(token)) {
            redirectAttributes.addFlashAttribute("errorMessage", "لینک بازیابی نامعتبر یا منقضی شده است.");
            return "redirect:/auth/forgot-password";
        }
        
        // Get user info for display
        User user = passwordResetService.getUserByResetToken(token);
        if (user != null) {
            model.addAttribute("userFullName", user.getFirstName() + " " + user.getLastName());
            model.addAttribute("userEmail", user.getEmail());
        }
        
        model.addAttribute("token", token);
        return "auth/reset-password";
    }
    
    /**
     * Process reset password form
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        
        try {
            // Validate password confirmation
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "رمز عبور و تکرار آن یکسان نیست");
                return "redirect:/auth/reset-password?token=" + token;
            }
            
            // Validate password strength
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "رمز عبور باید حداقل 6 کاراکتر باشد");
                return "redirect:/auth/reset-password?token=" + token;
            }
            
            String result = passwordResetService.resetPassword(token, newPassword);
            
            if (result.contains("موفقیت")) {
                redirectAttributes.addFlashAttribute("successMessage", result);
                return "redirect:/auth/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
                return "redirect:/auth/reset-password?token=" + token;
            }
            
        } catch (Exception e) {
            log.error("خطا در پردازش تغییر رمز عبور: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "خطایی رخ داد. لطفاً بعداً تلاش کنید.");
            return "redirect:/auth/reset-password?token=" + token;
        }
    }
}
