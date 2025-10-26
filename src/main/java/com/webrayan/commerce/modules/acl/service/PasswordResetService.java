package com.webrayan.commerce.modules.acl.service;

import com.webrayan.commerce.modules.acl.entity.User;

/**
 * Service for password reset functionality
 */
public interface PasswordResetService {
    
    /**
     * Initiate password reset process by sending email
     * @param email User's email address
     * @return Success message
     */
    String initiatePasswordReset(String email);
    
    /**
     * Validate password reset token
     * @param token Reset token
     * @return True if token is valid
     */
    boolean validateResetToken(String token);
    
    /**
     * Reset password using token
     * @param token Reset token
     * @param newPassword New password
     * @return Success message
     */
    String resetPassword(String token, String newPassword);
    
    /**
     * Get user by reset token
     * @param token Reset token
     * @return User associated with token
     */
    User getUserByResetToken(String token);
    
    /**
     * Check if user has reached reset attempt limit
     * @param email User's email
     * @return True if limit reached
     */
    boolean hasReachedResetLimit(String email);
    
    /**
     * Clean up expired tokens (scheduled task)
     */
    void cleanupExpiredTokens();
    
    /**
     * Change password for authenticated user
     * @param user Current user
     * @param currentPassword Current password for validation
     * @param newPassword New password
     * @return Success message
     */
    String changeUserPassword(User user, String currentPassword, String newPassword);
}
