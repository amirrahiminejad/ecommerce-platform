package com.webrayan.commerce.modules.acl.repository;

import com.webrayan.commerce.modules.acl.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionName(Permission.PermissionName permissionName);
    
    List<Permission> findByIsActive(Boolean isActive);
    
    List<Permission> findByResource(String resource);
    
    List<Permission> findByAction(String action);
    
    List<Permission> findByResourceAndAction(String resource, String action);
    
    List<Permission> findByPermissionNameIn(Set<Permission.PermissionName> permissionNames);
    
    @Query("SELECT p FROM Permission p WHERE p.isActive = true")
    List<Permission> findAllActivePermissions();
    
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.isActive = true")
    List<Permission> findActivePermissionsByResource(@Param("resource") String resource);
    
    Boolean existsByPermissionName(Permission.PermissionName permissionName);
}
