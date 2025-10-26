package com.webrayan.store.modules.acl.controller;

import com.webrayan.store.core.common.controller.BaseController;
import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "User Management", description = "API های مدیریت کاربران")
@RestController
@RequestMapping("/api/acl/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController extends BaseController {
    
    private final UserService userService;

    @Operation(
        summary = "دریافت لیست کاربران",
        description = "دریافت لیست تمام کاربران سیستم (فقط برای ادمین)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "لیست کاربران"),
        @ApiResponse(responseCode = "401", description = "احراز هویت نشده"),
        @ApiResponse(responseCode = "403", description = "دسترسی مجاز نیست")
    })
    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:READ')")
    public ResponseEntity<Map<String, Object>> getAllUsers(Pageable pageable) {
        try {
            List<User> users = userService.getAllUsers();
            return createSuccessResponse(users);
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در دریافت لیست کاربران: " + e.getMessage());
        }
    }

    @Operation(
        summary = "دریافت اطلاعات کاربر",
        description = "دریافت اطلاعات یک کاربر خاص با شناسه"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:READ') or #id == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserById(
        @Parameter(description = "شناسه کاربر", required = true) @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> createSuccessResponse(user))
                .orElse(createNotFoundResponse("کاربر پیدا نشد"));
    }

    @Operation(
        summary = "ایجاد کاربر جدید",
        description = "ایجاد کاربر جدید توسط ادمین سیستم"
    )
    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:CREATE')")
    public ResponseEntity<Map<String, Object>> createUser(
            @Parameter(description = "نام کاربری", required = true) @RequestParam String username,
            @Parameter(description = "آدرس ایمیل", required = true) @RequestParam String email,
            @Parameter(description = "رمز عبور", required = true) @RequestParam String password,
            @Parameter(description = "نام", required = true) @RequestParam String firstName,
            @Parameter(description = "نام خانوادگی", required = true) @RequestParam String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Set<Role.RoleName> roleNames) {
        try {
            User user = userService.createUser(username, email, password, firstName, lastName, phoneNumber, roleNames);
            return createSuccessResponse(user, "کاربر با موفقیت ایجاد شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در ایجاد کاربر: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:UPDATE') or #id == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email) {
        try {
            User user = userService.updateUser(id, firstName, lastName, phoneNumber, email);
            return createSuccessResponse(user, "کاربر با موفقیت به‌روزرسانی شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در به‌روزرسانی کاربر: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:MANAGE_ROLES')")
    public ResponseEntity<Map<String, Object>> assignRoles(@PathVariable Long id, @RequestBody Set<Role.RoleName> roleNames) {
        try {
            User user = userService.assignRoles(id, roleNames);
            return createSuccessResponse(user, "نقش‌های کاربر با موفقیت به‌روزرسانی شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در تخصیص نقش‌ها: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:MANAGE_ROLES')")
    public ResponseEntity<Map<String, Object>> addRole(@PathVariable Long id, @PathVariable Role.RoleName roleName) {
        try {
            User user = userService.addRole(id, roleName);
            return createSuccessResponse(user, "نقش با موفقیت اضافه شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در اضافه کردن نقش: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:MANAGE_ROLES')")
    public ResponseEntity<Map<String, Object>> removeRole(@PathVariable Long id, @PathVariable Role.RoleName roleName) {
        try {
            User user = userService.removeRole(id, roleName);
            return createSuccessResponse(user, "نقش با موفقیت حذف شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در حذف نقش: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("#id == authentication.principal.id or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            userService.changePassword(id, oldPassword, newPassword);
            return createSuccessResponse("رمز عبور با موفقیت تغییر کرد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در تغییر رمز عبور: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:UPDATE')")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            userService.resetPassword(id, newPassword);
            return createSuccessResponse("رمز عبور با موفقیت بازنشانی شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در بازنشانی رمز عبور: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:UPDATE')")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@PathVariable Long id, @RequestParam User.UserStatus status) {
        try {
            User user = userService.updateUserStatus(id, status);
            return createSuccessResponse(user, "وضعیت کاربر با موفقیت به‌روزرسانی شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در به‌روزرسانی وضعیت: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/verify-email")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> verifyEmail(@PathVariable Long id) {
        try {
            User user = userService.verifyEmail(id);
            return createSuccessResponse(user, "ایمیل با موفقیت تأیید شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در تأیید ایمیل: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/verify-phone")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> verifyPhone(@PathVariable Long id) {
        try {
            User user = userService.verifyPhone(id);
            return createSuccessResponse(user, "شماره تلفن با موفقیت تأیید شد");
        } catch (RuntimeException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در تأیید شماره تلفن: " + e.getMessage());
        }
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:READ')")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable Role.RoleName roleName) {
        try {
            List<User> users = userService.getUsersByRole(roleName);
            return createSuccessResponse(users);
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در دریافت کاربران: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('USER:READ')")
    public ResponseEntity<Map<String, Object>> getUsersByStatus(@PathVariable User.UserStatus status) {
        try {
            List<User> users = userService.getUsersByStatus(status);
            return createSuccessResponse(users);
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در دریافت کاربران: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/permissions/{resource}/{action}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> checkUserPermission(
            @PathVariable Long id,
            @PathVariable String resource,
            @PathVariable String action) {
        try {
            boolean hasPermission = userService.hasPermission(id, resource, action);
            return createSuccessResponse(Map.of("hasPermission", hasPermission));
        } catch (Exception e) {
            return createInternalErrorResponse("خطا در بررسی مجوز: " + e.getMessage());
        }
    }
}
