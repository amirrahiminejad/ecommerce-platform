package com.webrayan.commerce.core.util;

import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.service.CustomOAuth2User;
import com.webrayan.commerce.modules.acl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserService userService;

    /**
     * دریافت کاربر فعلی از Authentication (OAuth2 یا Regular)
     */
    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }

        // OAuth2 Authentication
        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
            Object principal = oauth2Token.getPrincipal();
            
            if (principal instanceof CustomOAuth2User) {
                CustomOAuth2User oauth2User = (CustomOAuth2User) principal;
                return Optional.of(oauth2User.getUser());
            }
        }

        // Regular Username/Password Authentication
        String username = auth.getName();
        return userService.getUserByUsername(username);
    }

    /**
     * دریافت ID کاربر فعلی
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(User::getId);
    }

    /**
     * دریافت ایمیل کاربر فعلی
     */
    public Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(User::getEmail);
    }

    /**
     * بررسی اینکه آیا کاربر فعلی OAuth2 user است یا نه
     */
    public boolean isOAuth2User() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof OAuth2AuthenticationToken;
    }

    /**
     * بررسی اینکه آیا کاربر وارد شده است یا نه
     */
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }
}
