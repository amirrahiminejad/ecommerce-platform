package com.webrayan.commerce.component.data.initializers;

import com.webrayan.commerce.modules.acl.entity.Permission;
import com.webrayan.commerce.modules.acl.entity.Role;
import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.repository.PermissionRepository;
import com.webrayan.commerce.modules.acl.repository.RoleRepository;
import com.webrayan.commerce.modules.acl.repository.UserRepository;
import com.webrayan.commerce.modules.acl.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityDataInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void initialize() {
        log.info("🔐 Starting security data initialization...");
        
        initializePermissions();
        initializeRoles();
        initializeDefaultAdminUser();
        
        log.info("✅ Security data initialized successfully");
    }

    private void initializePermissions() {
        log.info("📋 Creating default permissions...");
        
        int createdCount = 0;
        for (Permission.PermissionName permissionName : Permission.PermissionName.values()) {
            if (!permissionRepository.existsByPermissionName(permissionName)) {
                Permission permission = new Permission();
                permission.setPermissionName(permissionName);
                permission.setDisplayName(permissionName.getPersianName());
                permission.setResource(permissionName.getResource());
                permission.setAction(permissionName.getAction());
                permission.setDescription("Permission for " + permissionName.getPersianName());
                permission.setIsActive(true);
                
                permissionRepository.save(permission);
                createdCount++;
                log.debug("Permission {} created", permissionName.getPersianName());
            }
        }
        
        log.info("✅ {} permissions created", createdCount);
    }

    private void initializeRoles() {
        log.info("👥 Creating default roles...");
        
        roleService.createDefaultRoles();
        
        log.info("✅ Default roles created successfully");
    }

    private void initializeDefaultAdminUser() {
        log.info("👤 Creating default admin user...");
        
        if (!userRepository.existsByUsername("admin")) {
            // Find SYSTEM_ADMIN role
            Role adminRole = roleRepository.findByRoleName(Role.RoleName.SYSTEM_ADMIN)
                    .orElseThrow(() -> new RuntimeException("SYSTEM_ADMIN role not found"));
            
            // Create new admin user
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@iran-commerce.com");
            adminUser.setPassword(passwordEncoder.encode("123"));
            adminUser.setFirstName("System");
            adminUser.setLastName("Administrator");
            adminUser.setStatus(User.UserStatus.ACTIVE);
            adminUser.setEmailVerified(true);
            adminUser.setRoles(Set.of(adminRole));
            
            userRepository.save(adminUser);
            log.info("✅ Admin user created with username 'admin' and password '123'");
        } else {
            log.info("ℹ️ Admin user already exists");
        }
    }
}
