package com.webrayan.store.modules.acl.service.impl;

import com.webrayan.store.core.service.EmailService;
import com.webrayan.store.modules.acl.entity.PasswordResetToken;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.repository.PasswordResetTokenRepository;
import com.webrayan.store.modules.acl.repository.UserRepository;
import com.webrayan.store.modules.acl.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Implementation of PasswordResetService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.password-reset.token-expiration:900000}") // 15 minutes default
    private long tokenExpirationTime;
    
    @Value("${app.password-reset.max-attempts:3}")
    private int maxResetAttempts;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Override
    public String initiatePasswordReset(String email) {
        log.info("درخواست بازیابی رمز عبور برای: {}", email);
        
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("کاربر با ایمیل {} پیدا نشد", email);
            // Don't reveal if email exists or not for security
            return "اگر ایمیل شما در سیستم موجود باشد، لینک بازیابی رمز عبور ارسال خواهد شد.";
        }
        
        User user = userOpt.get();
        
        // Check if user account is active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            log.warn("حساب کاربری {} فعال نیست", email);
            return "حساب کاربری شما فعال نیست. لطفاً با پشتیبانی تماس بگیرید.";
        }
        
        // Check rate limiting
        if (hasReachedResetLimit(email)) {
            log.warn("محدودیت تعداد درخواست بازیابی برای {} رسیده", email);
            return "تعداد درخواست‌های بازیابی رمز عبور بیش از حد مجاز است. لطفاً بعداً تلاش کنید.";
        }
        
        try {
            // Invalidate existing tokens for this user
            tokenRepository.markAllUserTokensAsUsed(user, LocalDateTime.now());
            
            // Generate new token
            String token = generateSecureToken();
            LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(tokenExpirationTime / 1000);
            
            // Create and save token
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(expiryDate);
            
            tokenRepository.save(resetToken);
            
            // Send email
            String userFullName = user.getFirstName() + " " + user.getLastName();
            emailService.sendPasswordResetEmail(email, token, userFullName);
            
            log.info("ایمیل بازیابی رمز عبور برای {} ارسال شد", email);
            return "لینک بازیابی رمز عبور به ایمیل شما ارسال شد.";
            
        } catch (Exception e) {
            log.error("خطا در ارسال ایمیل بازیابی رمز عبور برای {}: {}", email, e.getMessage(), e);
            return "خطایی در ارسال ایمیل رخ داد. لطفاً بعداً تلاش کنید.";
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        return tokenRepository.existsByTokenAndIsValidAndNotExpired(token, LocalDateTime.now());
    }
    
    @Override
    public String resetPassword(String token, String newPassword) {
        log.info("تلاش برای تغییر رمز عبور با token");
        
        if (!validateResetToken(token)) {
            log.warn("Token نامعتبر یا منقضی شده");
            return "لینک بازیابی رمز عبور نامعتبر یا منقضی شده است.";
        }
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            log.warn("Token پیدا نشد: {}", token.substring(0, Math.min(10, token.length())));
            return "لینک بازیابی رمز عبور نامعتبر است.";
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        User user = resetToken.getUser();
        
        try {
            // Update user password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            // Mark token as used
            resetToken.markAsUsed();
            tokenRepository.save(resetToken);
            
            // Send confirmation email
            String userFullName = user.getFirstName() + " " + user.getLastName();
            emailService.sendPasswordChangeConfirmationEmail(user.getEmail(), userFullName);
            
            log.info("رمز عبور کاربر {} با موفقیت تغییر کرد", user.getEmail());
            return "رمز عبور شما با موفقیت تغییر کرد.";
            
        } catch (Exception e) {
            log.error("خطا در تغییر رمز عبور برای کاربر {}: {}", user.getEmail(), e.getMessage(), e);
            return "خطایی در تغییر رمز عبور رخ داد. لطفاً بعداً تلاش کنید.";
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserByResetToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && !t.isExpired())
                .map(PasswordResetToken::getUser)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasReachedResetLimit(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        long recentTokenCount = tokenRepository.countRecentTokensByUser(user, oneHourAgo);
        return recentTokenCount >= maxResetAttempts;
    }
    
    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        log.debug("پاک‌سازی توکن‌های منقضی شده");
        
        try {
            tokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.debug("توکن‌های منقضی شده پاک شدند");
        } catch (Exception e) {
            log.error("خطا در پاک‌سازی توکن‌های منقضی: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public String changeUserPassword(User user, String currentPassword, String newPassword) {
        log.info("درخواست تغییر رمز عبور برای کاربر: {}", user.getUsername());
        
        try {
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                log.warn("رمز عبور فعلی نادرست برای کاربر: {}", user.getUsername());
                return "رمز عبور فعلی صحیح نمی‌باشد.";
            }
            
            // Check if new password is same as current
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                log.warn("رمز عبور جدید مشابه رمز فعلی برای کاربر: {}", user.getUsername());
                return "رمز عبور جدید نمی‌تواند مشابه رمز عبور فعلی باشد.";
            }
            
            // Encode and set new password
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Save user
            userRepository.save(user);
            
            // Send security notification email
            try {
                String userFullName = user.getFirstName() + " " + user.getLastName();
                emailService.sendPasswordChangeNotification(user.getEmail(), userFullName);
            } catch (Exception e) {
                log.warn("خطا در ارسال ایمیل اطلاع رسانی تغییر رمز عبور: {}", e.getMessage());
                // Don't fail the password change if email fails
            }
            
            log.info("رمز عبور کاربر {} با موفقیت تغییر یافت", user.getUsername());
            return "رمز عبور شما با موفقیت تغییر یافت.";
            
        } catch (Exception e) {
            log.error("خطا در تغییر رمز عبور برای کاربر {}: {}", user.getUsername(), e.getMessage(), e);
            return "خطایی رخ داد. لطفاً بعداً تلاش کنید.";
        }
    }

    /**
     * Generate cryptographically secure random token
     */
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32]; // 256-bit token
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
