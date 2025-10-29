package com.webrayan.store.modules.acl.repository;

import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserUuid(UUID userUuid);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByEmail(String email);

    Boolean existsByNationalCode(String nationalCode);

    List<User> findByStatus(User.UserStatus status);

    Long countByStatus(User.UserStatus status);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findByRole(@Param("roleName") Role.RoleName roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName IN :roleNames")
    List<User> findByRoles(@Param("roleNames") List<Role.RoleName> roleNames);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByFirstNameOrLastNameContaining(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber")
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.emailVerified = :emailVerified")
    List<User> findByStatusAndEmailVerified(@Param("status") User.UserStatus status,
                                            @Param("emailVerified") Boolean emailVerified);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    Long countUsersByRole(@Param("roleName") Role.RoleName roleName);

//    @Query("SELECT u FROM User u WHERE u.createdAt >= CURRENT_DATE - :days")
//    List<User> findUsersCreatedInLastDays(@Param("days") int days);

    // Pagination methods for admin panel
    
    /**
     * دریافت کاربران بر اساس وضعیت status با pagination
     */
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);

    /**
     * جستجو در نام کاربری یا ایمیل با pagination
     */
    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);

    /**
     * جستجو در نام کاربری یا ایمیل بر اساس وضعیت status با pagination
     */
    @Query("SELECT u FROM User u WHERE (u.username LIKE %:username% OR u.email LIKE %:email%) AND u.status = :status")
    Page<User> findByUsernameContainingOrEmailContainingAndStatus(@Param("username") String username, 
                                                                  @Param("email") String email, 
                                                                  @Param("status") User.UserStatus status, 
                                                                  Pageable pageable);

    // Methods for affiliate management
    
    /**
     * دریافت کاربران بر اساس نقش با pagination
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    Page<User> findByRole(@Param("roleName") Role.RoleName roleName, Pageable pageable);

    /**
     * دریافت کاربران بر اساس نقش و وضعیت با pagination
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND u.status = :status")
    Page<User> findByRoleAndStatus(@Param("roleName") Role.RoleName roleName, 
                                   @Param("status") User.UserStatus status, 
                                   Pageable pageable);

    /**
     * جستجو در کاربران بر اساس نقش با pagination
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND " +
           "(u.username LIKE %:search% OR u.email LIKE %:search% OR " +
           "u.firstName LIKE %:search% OR u.lastName LIKE %:search%)")
    Page<User> findByRoleAndSearch(@Param("roleName") Role.RoleName roleName, 
                                   @Param("search") String search, 
                                   Pageable pageable);

    /**
     * جستجو در کاربران بر اساس نقش و وضعیت با pagination
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND u.status = :status AND " +
           "(u.username LIKE %:search% OR u.email LIKE %:search% OR " +
           "u.firstName LIKE %:search% OR u.lastName LIKE %:search%)")
    Page<User> findByRoleAndSearchAndStatus(@Param("roleName") Role.RoleName roleName, 
                                            @Param("search") String search, 
                                            @Param("status") User.UserStatus status, 
                                            Pageable pageable);

}