package com.webrayan.commerce.core.security;

import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Autowired
    private UserService userService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        String permissionStr = permission.toString();
        
        log.debug("بررسی مجوز {} برای کاربر {}", permissionStr, username);
        
        return checkUserPermission(username, permissionStr);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        String resource = targetType;
        String action = permission.toString();
        
        log.debug("بررسی مجوز {}:{} برای کاربر {} روی آبجکت {}", resource, action, username, targetId);
        
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        return userService.hasPermission(userOpt.get().getId(), resource, action);
    }
    
    private boolean checkUserPermission(String username, String permission) {
        try {
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            
            // اگر مجوز شامل : باشد، آن را به resource:action تقسیم کن
            if (permission.contains(":")) {
                String[] parts = permission.split(":");
                if (parts.length == 2) {
                    return userService.hasPermission(user.getId(), parts[0], parts[1]);
                }
            }
            
            // در غیر این صورت، بررسی کن که آیا کاربر نقش مورد نظر را دارد یا خیر
            return user.getRoles().stream()
                    .anyMatch(role -> role.getIsActive() && 
                             ("ROLE_" + role.getRoleName().name()).equals(permission));
                             
        } catch (Exception e) {
            log.error("خطا در بررسی مجوز {} برای کاربر {}: {}", permission, username, e.getMessage());
            return false;
        }
    }
}
