package com.webrayan.commerce.modules.acl.repository;

import com.webrayan.commerce.modules.acl.entity.PasswordResetToken;
import com.webrayan.commerce.modules.acl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find token by token string
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Find all tokens for a user
     */
    List<PasswordResetToken> findByUser(User user);
    
    /**
     * Find valid (not used and not expired) tokens for a user
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user = :user AND t.isUsed = false AND t.expiryDate > :now")
    List<PasswordResetToken> findValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Delete expired tokens
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Mark all user tokens as used
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.isUsed = true, t.usedAt = :now WHERE t.user = :user AND t.isUsed = false")
    void markAllUserTokensAsUsed(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Count recent tokens for a user (for rate limiting)
     */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.user = :user AND t.createdAt > :since")
    long countRecentTokensByUser(@Param("user") User user, @Param("since") LocalDateTime since);
    
    /**
     * Check if token exists and is valid
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM PasswordResetToken t " +
           "WHERE t.token = :token AND t.isUsed = false AND t.expiryDate > :now")
    boolean existsByTokenAndIsValidAndNotExpired(@Param("token") String token, @Param("now") LocalDateTime now);
}
