package com.webrayan.commerce.core.security;

import com.webrayan.commerce.modules.acl.entity.Permission;
import com.webrayan.commerce.modules.acl.entity.Role;
import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service("customUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("بارگذاری کاربر: {}", username);
        
        // جستجو با username یا email
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر پیدا نشد: " + username));
        
        log.debug("کاربر {} با {} نقش پیدا شد", user.getUsername(), 
                user.getRoles() != null ? user.getRoles().size() : 0);
        
        return createUserPrincipal(user);
    }
    
    private UserDetails createUserPrincipal(User user) {
        Collection<GrantedAuthority> authorities = getUserAuthorities(user);
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.getStatus() == User.UserStatus.BANNED)
                .credentialsExpired(false)
                .disabled(user.getStatus() == User.UserStatus.INACTIVE || user.getStatus() == User.UserStatus.BANNED)
                .build();
    }
    
    private Collection<GrantedAuthority> getUserAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getIsActive()) {
                    // اضافه کردن نقش با پیشوند ROLE_
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()));
                    
                    // اضافه کردن مجوزهای نقش
                    if (role.getPermissions() != null) {
                        for (Permission permission : role.getPermissions()) {
                            if (permission.getIsActive()) {
                                String permissionStr = permission.getResource() + ":" + permission.getAction();
                                authorities.add(new SimpleGrantedAuthority(permissionStr));
                            }
                        }
                    }
                }
            }
        }
        
        log.debug("کاربر {} دارای {} مجوز است", user.getUsername(), authorities.size());
        return authorities;
    }
}
