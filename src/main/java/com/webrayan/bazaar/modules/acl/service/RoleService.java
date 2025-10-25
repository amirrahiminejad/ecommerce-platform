package com.webrayan.bazaar.modules.acl.service;

import com.webrayan.bazaar.modules.acl.entity.Permission;
import com.webrayan.bazaar.modules.acl.entity.Role;
import com.webrayan.bazaar.modules.acl.repository.PermissionRepository;
import com.webrayan.bazaar.modules.acl.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> getActiveRoles() {
        return roleRepository.findAllActiveRoles();
    }

    public Optional<Role> getRoleByName(Role.RoleName roleName) {return roleRepository.findByRoleName(roleName);}

    public Optional<Role> getRoleById(Long roleID) {
        return roleRepository.findById(roleID);
    }


    public Role createRole(Role.RoleName roleName, String displayName,
                           Set<Permission.PermissionName> permissionNames) {

        if (roleRepository.existsByRoleName(roleName)) {
            throw new RuntimeException("نقش با این نام قبلاً وجود دارد: " + roleName.getPersianName());
        }

        Role role = new Role();
        role.setRoleName(roleName);
        role.setDisplayName(displayName);
        role.setIsActive(true);

        if (permissionNames != null && !permissionNames.isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (Permission.PermissionName permissionName : permissionNames) {
                permissionRepository.findByPermissionName(permissionName)
                        .ifPresent(permissions::add);
            }
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    public Role updateRole(Long roleId, String displayName, String description,
                           Set<Permission.PermissionName> permissionNames) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("نقش یافت نشد"));

        role.setDisplayName(displayName);

        if (permissionNames != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Permission.PermissionName permissionName : permissionNames) {
                permissionRepository.findByPermissionName(permissionName)
                        .ifPresent(permissions::add);
            }
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    public void deactivateRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("نقش یافت نشد"));

        role.setIsActive(false);
        roleRepository.save(role);

        log.info("نقش {} غیرفعال شد", role.getDisplayName());
    }

    public void activateRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("نقش یافت نشد"));

        role.setIsActive(true);
        roleRepository.save(role);

        log.info("نقش {} فعال شد", role.getDisplayName());
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllActivePermissions();
    }

    public List<Permission> getPermissionsByResource(String resource) {
        return permissionRepository.findActivePermissionsByResource(resource);
    }

    /**
     * ایجاد نقش‌های پیش‌فرض سیستم
     */
    @Transactional
    public void createDefaultRoles() {

        // نقش مدیر سیستم
        createSystemAdminRole();

        // نقش کارمند
        createEmployeeRole();

        // نقش مشتری
        createCustomerRole();

        // نقش بازاریاب
        createAffiliateRole();

    }

    private void createSystemAdminRole() {
        if (!roleRepository.existsByRoleName(Role.RoleName.SYSTEM_ADMIN)) {
            Set<Permission.PermissionName> adminPermissions = Set.of(
                    Permission.PermissionName.USER_CREATE,
                    Permission.PermissionName.USER_READ,
                    Permission.PermissionName.USER_UPDATE,
                    Permission.PermissionName.USER_DELETE,
                    Permission.PermissionName.USER_MANAGE_ROLES,
                    Permission.PermissionName.PRODUCT_CREATE,
                    Permission.PermissionName.PRODUCT_READ,
                    Permission.PermissionName.PRODUCT_UPDATE,
                    Permission.PermissionName.PRODUCT_DELETE,
                    Permission.PermissionName.ORDER_READ,
                    Permission.PermissionName.ORDER_UPDATE,
                    Permission.PermissionName.ORDER_CANCEL,
                    Permission.PermissionName.SYSTEM_CONFIG,
                    Permission.PermissionName.SYSTEM_BACKUP,
                    Permission.PermissionName.SYSTEM_MAINTENANCE,
                    Permission.PermissionName.REPORT_SALES,
                    Permission.PermissionName.REPORT_FINANCIAL,
                    Permission.PermissionName.REPORT_USER
            );

            createRole(Role.RoleName.SYSTEM_ADMIN, "مدیر سیستم", adminPermissions);
        }
    }

    private void createEmployeeRole() {
        if (!roleRepository.existsByRoleName(Role.RoleName.EMPLOYEE)) {
            Set<Permission.PermissionName> employeePermissions = Set.of(
                    Permission.PermissionName.PRODUCT_READ,
                    Permission.PermissionName.PRODUCT_UPDATE,
                    Permission.PermissionName.ORDER_READ,
                    Permission.PermissionName.ORDER_UPDATE,
                    Permission.PermissionName.ORDER_FULFILL,
                    Permission.PermissionName.INVENTORY_READ,
                    Permission.PermissionName.INVENTORY_UPDATE,
                    Permission.PermissionName.USER_READ
            );

            createRole(Role.RoleName.EMPLOYEE, "کارمند", employeePermissions);
        }
    }

    private void createCustomerRole() {
        if (!roleRepository.existsByRoleName(Role.RoleName.CUSTOMER)) {
            Set<Permission.PermissionName> customerPermissions = Set.of(
                    Permission.PermissionName.PRODUCT_READ,
                    Permission.PermissionName.ORDER_CREATE,
                    Permission.PermissionName.ORDER_READ
            );

            createRole(Role.RoleName.CUSTOMER, "مشتری", customerPermissions);
        }
    }

    private void createAffiliateRole() {
        if (!roleRepository.existsByRoleName(Role.RoleName.AFFILIATE)) {
            Set<Permission.PermissionName> affiliatePermissions = Set.of(
                    Permission.PermissionName.PRODUCT_READ,
                    Permission.PermissionName.ORDER_READ,
                    Permission.PermissionName.AFFILIATE_READ,
                    Permission.PermissionName.REPORT_SALES
            );

            createRole(Role.RoleName.AFFILIATE, "بازاریاب", affiliatePermissions);
        }
    }

}
