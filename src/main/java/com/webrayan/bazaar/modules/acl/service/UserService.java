package com.webrayan.bazaar.modules.acl.service;

import com.webrayan.bazaar.modules.acl.entity.Role;
import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.modules.acl.repository.RoleRepository;
import com.webrayan.bazaar.modules.acl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUuid(UUID userUuid) {
        return userRepository.findByUserUuid(userUuid);
    }

    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public User createUser(String username, String email, String password,
                           String firstName, String lastName, String phoneNumber,
                           Set<Role.RoleName> roleNames) {

        // بررسی تکراری بودن
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("نام کاربری قبلاً استفاده شده است");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("ایمیل قبلاً استفاده شده است");
        }

        User user = new User();
        user.setUserUuid(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        //user.setStatus(User.UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        // اضافه کردن نقش‌ها
        if (roleNames != null && !roleNames.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Role.RoleName roleName : roleNames) {
                roleRepository.findByRoleName(roleName)
                        .ifPresent(roles::add);
            }
            user.setRoles(roles);
        } else {
            // اگر نقشی تعیین نشده، نقش مشتری را اضافه کن
            roleRepository.findByRoleName(Role.RoleName.CUSTOMER)
                    .ifPresent(role -> user.setRoles(Set.of(role)));
        }

        User savedUser = userRepository.save(user);
        log.info("کاربر جدید ایجاد شد: {}", savedUser.getUsername());

        return savedUser;
    }

    public User updateUser(Long userId, String firstName, String lastName,
                           String phoneNumber, String email) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        // بررسی تکراری بودن ایمیل
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("ایمیل قبلاً استفاده شده است");
            }
            user.setEmail(email);
            user.setEmailVerified(false); // نیاز به تأیید مجدد
        }

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
            user.setPhoneVerified(false); // نیاز به تأیید مجدد
        }

        return userRepository.save(user);
    }

    public User updateSocialMediaLinks(Long userId, String linkdin, String facebook, String instagram, String whatsapp, String telegram) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        // Clean and validate URLs/usernames
        user.setLinkdin(cleanUrl(linkdin));
        user.setFacebook(cleanUrl(facebook));
        user.setInstagram(cleanUrl(instagram));
        user.setWhatsapp(cleanMessagingApp(whatsapp, "whatsapp"));
        user.setTelegram(cleanMessagingApp(telegram, "telegram"));
        
        user.setUpdatedAt(LocalDateTime.now());
        
        log.info("Social media links updated for user: {}", user.getUsername());
        return userRepository.save(user);
    }

    private String cleanUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        url = url.trim();
        // If URL doesn't start with http:// or https://, add https://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    private String cleanMessagingApp(String input, String appType) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        input = input.trim();
        
        if ("whatsapp".equals(appType)) {
            // اگر شماره تلفن است، فرمت WhatsApp کن
            if (input.matches("^[+]?[0-9\\s-()]+$")) {
                String cleanNumber = input.replaceAll("[\\s-()]", "");
                if (!cleanNumber.startsWith("+")) {
                    cleanNumber = "+" + cleanNumber;
                }
                return "https://wa.me/" + cleanNumber.substring(1);
            }
            // اگر لینک است، تمیز کن
            if (!input.startsWith("http")) {
                return "https://wa.me/" + input;
            }
        } else if ("telegram".equals(appType)) {
            // اگر username است
            if (!input.startsWith("@") && !input.startsWith("http")) {
                return "https://t.me/" + input;
            }
            if (input.startsWith("@")) {
                return "https://t.me/" + input.substring(1);
            }
        }
        
        return cleanUrl(input);
    }

    public User assignRoles(Long userId, Set<Role.RoleName> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        Set<Role> roles = new HashSet<>();
        for (Role.RoleName roleName : roleNames) {
            roleRepository.findByRoleName(roleName)
                    .ifPresent(roles::add);
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        log.info("نقش‌های کاربر {} به‌روزرسانی شد", user.getUsername());
        return savedUser;
    }

    public User addRole(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("نقش یافت نشد"));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User removeRole(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        if (user.getRoles() != null) {
            user.getRoles().removeIf(role -> role.getRoleName() == roleName);
            return userRepository.save(user);
        }

        return user;
    }

    public User changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("رمز عبور فعلی اشتباه است");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User updateUserStatus(Long userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        user.setStatus(status);
        return userRepository.save(user);
    }

    public User verifyEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    public User verifyPhone(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        user.setPhoneVerified(true);
        return userRepository.save(user);
    }

    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<User> getUsersByRole(Role.RoleName roleName) {
        return userRepository.findByRole(roleName);
    }

    public List<User> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status);
    }

    public Long countUsersByRole(Role.RoleName roleName) {
        return userRepository.countUsersByRole(roleName);
    }

    public boolean hasPermission(Long userId, String resource, String action) {
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null || user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
                .filter(role -> role.getIsActive())
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> permission.getIsActive())
                .anyMatch(permission ->
                        permission.getResource().equals(resource) &&
                                permission.getAction().equals(action));
    }

    /**
     * شمارش کل کاربران
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * شمارش کاربران فعال
     */
    public long countActiveUsers() {
        return userRepository.countByStatus(User.UserStatus.ACTIVE);
    }

    // Pagination methods for admin panel
    
    /**
     * دریافت تمام کاربران با pagination
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * دریافت کاربران بر اساس وضعیت status با pagination
     */
    public Page<User> findByStatus(User.UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    /**
     * جستجو در نام کاربری یا ایمیل با pagination
     */
    public Page<User> findByUsernameOrEmailContaining(String search, Pageable pageable) {
        return userRepository.findByUsernameContainingOrEmailContaining(search, search, pageable);
    }

    /**
     * جستجو در نام کاربری یا ایمیل بر اساس وضعیت status با pagination
     */
    public Page<User> findByUsernameOrEmailContainingAndStatus(String search, User.UserStatus status, Pageable pageable) {
        return userRepository.findByUsernameContainingOrEmailContainingAndStatus(search, search, status, pageable);
    }

    /**
     * دریافت کاربر بر اساس ID
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"));
    }
}

