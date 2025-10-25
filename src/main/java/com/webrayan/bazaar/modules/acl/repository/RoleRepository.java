package com.webrayan.bazaar.modules.acl.repository;

import com.webrayan.bazaar.modules.acl.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(Role.RoleName roleName);

    List<Role> findByIsActive(Boolean isActive);

    List<Role> findByRoleNameIn(Set<Role.RoleName> roleNames);

    @Query("SELECT r FROM Role r WHERE r.isActive = true")
    List<Role> findAllActiveRoles();

//    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.permissionName = :permissionName AND r.isActive = true")
//    List<Role> findRolesByPermission(@Param("permissionName") Role.RoleName permissionName);

    Boolean existsByRoleName(Role.RoleName roleName);}
