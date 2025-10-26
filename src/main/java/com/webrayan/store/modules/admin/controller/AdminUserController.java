package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;
    
    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {
        
        // Create pageable with sort by creation date descending
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Get users based on filters
        Page<User> usersPage;
        
        if (search != null && !search.trim().isEmpty()) {
            if (status != null && !status.isEmpty()) {
                User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
                usersPage = userService.findByUsernameOrEmailContainingAndStatus(search.trim(), userStatus, pageable);
            } else {
                usersPage = userService.findByUsernameOrEmailContaining(search.trim(), pageable);
            }
        } else if (status != null && !status.isEmpty()) {
            User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
            usersPage = userService.findByStatus(userStatus, pageable);
        } else {
            usersPage = userService.findAll(pageable);
        }
        
        // Add attributes to model
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", usersPage.getNumber());
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        
        return "admin/pages/users";
    }
    
    @GetMapping("/view")
    public String viewUser(@RequestParam Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/pages/user-detail";
    }
    
    @GetMapping("/edit")
    public String editUser(@RequestParam Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/pages/user-edit";
    }
    
    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/pages/user-create";
    }
    
    @PostMapping("/create")
    public String createUser(@ModelAttribute User user,
                           @RequestParam String password,
                           @RequestParam(required = false) String[] roleNames,
                           RedirectAttributes redirectAttributes) {
        try {
            // تبدیل نقش‌ها
            Set<Role.RoleName> roles = new HashSet<>();
            if (roleNames != null) {
                roles = Arrays.stream(roleNames)
                        .map(Role.RoleName::valueOf)
                        .collect(Collectors.toSet());
            }
            
            // ایجاد کاربر
            User createdUser = userService.createUser(
                user.getUsername(),
                user.getEmail(),
                password,
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                roles
            );
            
            // تنظیم وضعیت
            if (user.getStatus() != null) {
                createdUser.setStatus(user.getStatus());
                userService.updateUserStatus(createdUser.getId(), user.getStatus());
            }
            
            redirectAttributes.addFlashAttribute("success", "کاربر با موفقیت ایجاد شد");
            return "redirect:/admin/users";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/create";
        }
    }
    
    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user,
                         @RequestParam(required = false) String[] roleNames,
                         @RequestParam(required = false) Boolean emailVerified,
                         @RequestParam(required = false) Boolean phoneVerified,
                         RedirectAttributes redirectAttributes) {
        try {
            // بروزرسانی اطلاعات پایه
            userService.updateUser(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getEmail()
            );
            
            // بروزرسانی وضعیت
            if (user.getStatus() != null) {
                userService.updateUserStatus(user.getId(), user.getStatus());
            }
            
            // بروزرسانی نقش‌ها
            if (roleNames != null) {
                Set<Role.RoleName> roles = Arrays.stream(roleNames)
                        .map(Role.RoleName::valueOf)
                        .collect(Collectors.toSet());
                userService.assignRoles(user.getId(), roles);
            }
            
            // بروزرسانی وضعیت تأیید
            if (emailVerified != null && emailVerified) {
                userService.verifyEmail(user.getId());
            }
            
            if (phoneVerified != null && phoneVerified) {
                userService.verifyPhone(user.getId());
            }
            
            redirectAttributes.addFlashAttribute("success", "کاربر با موفقیت ویرایش شد");
            return "redirect:/admin/users";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/edit?id=" + user.getId();
        }
    }
    
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long userId,
                              @RequestParam String newPassword,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("success", "رمز عبور کاربر با موفقیت تغییر یافت");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/users/view?id=" + userId;
    }
    
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            // به جای حذف واقعی، وضعیت را به DELETED تغییر می‌دهیم
            userService.updateUserStatus(id, User.UserStatus.DELETED);
            redirectAttributes.addFlashAttribute("success", "کاربر با موفقیت حذف شد");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
}
