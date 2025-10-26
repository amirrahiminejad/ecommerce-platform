package com.webrayan.store.modules.acl.controller;

import com.webrayan.store.core.common.controller.BaseController;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.repository.UserRepository;
import com.webrayan.store.modules.acl.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Password Reset", description = "API های مربوط به بازیابی رمز عبور")
@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController extends BaseController {
    
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    
    @Operation(
        summary = "درخواست بازیابی رمز عبور",
        description = "ارسال ایمیل بازیابی رمز عبور به کاربر"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "درخواست با موفقیت پردازش شد",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"لینک بازیابی رمز عبور به ایمیل شما ارسال شد.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "درخواست نامعتبر",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"ایمیل معتبر وارد کنید\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "429", 
            description = "تعداد درخواست‌ها بیش از حد",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"تعداد درخواست‌های بازیابی رمز عبور بیش از حد مجاز است\"}"
                )
            )
        )
    })
    @PostMapping("/request")
    public ResponseEntity<?> requestPasswordReset(
        @Parameter(description = "ایمیل کاربر", required = true)
        @RequestParam 
        @NotBlank(message = "ایمیل نمی‌تواند خالی باشد")
        @Email(message = "فرمت ایمیل صحیح نیست")
        String email,
        HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Log the request for security monitoring
            String clientIp = getClientIpAddress(request);
            log.info("درخواست بازیابی رمز عبور از IP: {} برای ایمیل: {}", clientIp, email);
            
            String result = passwordResetService.initiatePasswordReset(email.trim().toLowerCase());
            
            response.put("success", true);
            response.put("message", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("خطا در درخواست بازیابی رمز عبور برای {}: {}", email, e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "خطایی رخ داد. لطفاً بعداً تلاش کنید.");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(
        summary = "اعتبارسنجی توکن بازیابی",
        description = "بررسی معتبر بودن توکن بازیابی رمز عبور"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "وضعیت توکن",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Valid Token",
                        value = "{\"success\": true, \"valid\": true, \"message\": \"توکن معتبر است\"}"
                    ),
                    @ExampleObject(
                        name = "Invalid Token",
                        value = "{\"success\": true, \"valid\": false, \"message\": \"توکن نامعتبر یا منقضی شده\"}"
                    )
                }
            )
        )
    })
    @GetMapping("/validate")
    public ResponseEntity<?> validateResetToken(
        @Parameter(description = "توکن بازیابی", required = true)
        @RequestParam 
        @NotBlank(message = "توکن نمی‌تواند خالی باشد")
        String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isValid = passwordResetService.validateResetToken(token);
            
            response.put("success", true);
            response.put("valid", isValid);
            response.put("message", isValid ? "توکن معتبر است" : "توکن نامعتبر یا منقضی شده");
            
            // If valid, include user info (without sensitive data)
            if (isValid) {
                User user = passwordResetService.getUserByResetToken(token);
                if (user != null) {
                    response.put("user", Map.of(
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "email", user.getEmail()
                    ));
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("خطا در اعتبارسنجی توکن: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "خطایی رخ داد");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(
        summary = "تغییر رمز عبور",
        description = "تنظیم رمز عبور جدید با استفاده از توکن بازیابی"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "رمز عبور با موفقیت تغییر کرد",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"رمز عبور شما با موفقیت تغییر کرد.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "درخواست نامعتبر",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"توکن نامعتبر یا رمز عبور ضعیف است\"}"
                )
            )
        )
    })
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(
        @Parameter(description = "توکن بازیابی", required = true)
        @RequestParam 
        @NotBlank(message = "توکن نمی‌تواند خالی باشد")
        String token,
        
        @Parameter(description = "رمز عبور جدید", required = true)
        @RequestParam 
        @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
        @Size(min = 6, max = 50, message = "رمز عبور باید بین 6 تا 50 کاراکتر باشد")
        String newPassword,
        
        @Parameter(description = "تکرار رمز عبور جدید", required = true)
        @RequestParam 
        @NotBlank(message = "تکرار رمز عبور نمی‌تواند خالی باشد")
        String confirmPassword,
        
        HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate password confirmation
            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "رمز عبور و تکرار آن یکسان نیست");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Log the request for security monitoring
            String clientIp = getClientIpAddress(request);
            log.info("تلاش تغییر رمز عبور از IP: {}", clientIp);
            
            String result = passwordResetService.resetPassword(token, newPassword);
            
            // Check if reset was successful
            boolean success = result.contains("موفقیت");
            
            response.put("success", success);
            response.put("message", result);
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("خطا در تغییر رمز عبور: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "خطایی رخ داد. لطفاً بعداً تلاش کنید.");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(
        summary = "تغییر رمز عبور کاربر لاگین شده",
        description = "تغییر رمز عبور کاربر وارد شده به سیستم"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "رمز عبور با موفقیت تغییر یافت",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"رمز عبور با موفقیت تغییر یافت.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "خطا در ولیدیشن یا رمز عبور فعلی اشتباه",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"رمز عبور فعلی صحیح نمی‌باشد.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "کاربر وارد سیستم نشده است",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"برای این عملیات باید وارد سیستم شوید.\"}"
                )
            )
        )
    })
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
        @Parameter(description = "رمز عبور فعلی", required = true)
        @RequestParam 
        @NotBlank(message = "رمز عبور فعلی نمی‌تواند خالی باشد")
        String currentPassword,
        
        @Parameter(description = "رمز عبور جدید", required = true)
        @RequestParam 
        @NotBlank(message = "رمز عبور جدید نمی‌تواند خالی باشد")
        @Size(min = 6, max = 50, message = "رمز عبور باید بین 6 تا 50 کاراکتر باشد")
        String newPassword,
        
        @Parameter(description = "تکرار رمز عبور جدید", required = true)
        @RequestParam 
        @NotBlank(message = "تکرار رمز عبور نمی‌تواند خالی باشد")
        String confirmPassword,
        
        HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is authenticated
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "برای این عملیات باید وارد سیستم شوید.");
                return ResponseEntity.status(401).body(response);
            }
            
            // Validate password confirmation
            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "رمز عبور جدید و تکرار آن یکسان نیست");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Log the request for security monitoring
            String clientIp = getClientIpAddress(request);
            log.info("تلاش تغییر رمز عبور توسط کاربر {} از IP: {}", currentUser.getUsername(), clientIp);
            
            String result = passwordResetService.changeUserPassword(currentUser, currentPassword, newPassword);
            
            // Check if change was successful
            boolean success = result.contains("موفقیت");
            
            response.put("success", success);
            response.put("message", result);
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("خطا در تغییر رمز عبور: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "خطایی رخ داد. لطفاً بعداً تلاش کنید.");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElse(null);
            
        } catch (Exception e) {
            log.error("خطا در دریافت کاربر جاری: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
