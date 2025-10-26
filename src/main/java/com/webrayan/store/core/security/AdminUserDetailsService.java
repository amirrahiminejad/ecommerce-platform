package com.webrayan.store.core.security;

import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserDetailsService برای Admin Panel
 */
@Service("adminUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class AdminUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("بارگذاری کاربر برای Admin Panel: {}", username);
        
        User user = userService.getUserByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // بررسی اینکه کاربر دارای نقش ADMIN است یا نه

//        boolean hasAdminRole = user.getRoles().stream()
//                .anyMatch(role -> role.getRoleName() == Role.RoleName.SYSTEM_ADMIN);
//
//        if (!hasAdminRole) {
//            throw new UsernameNotFoundException("کاربر دسترسی ادمین ندارد: " + username);
//        }

        // بررسی وضعیت کاربر
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("حساب کاربری غیرفعال است: " + username);
        }

        Collection<GrantedAuthority> authorities = getAuthorities(user.getRoles());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * تبدیل نقش‌های کاربر به GrantedAuthority
     */
    private Collection<GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .filter(Role::getIsActive)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))
                .collect(Collectors.toList());
    }
}
