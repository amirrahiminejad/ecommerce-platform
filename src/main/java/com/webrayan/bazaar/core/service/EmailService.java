package com.webrayan.bazaar.core.service;

import jakarta.mail.MessagingException;

/**
 * Email service interface for sending various types of emails
 */
public interface EmailService {
    
    /**
     * Send simple text email
     */
    void sendSimpleEmail(String to, String subject, String text);
    
    /**
     * Send HTML email
     */
    void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException;
    
    /**
     * Send password reset email
     */
    void sendPasswordResetEmail(String to, String resetToken, String userFullName);
    
    /**
     * Send email verification email
     */
    void sendEmailVerificationEmail(String to, String verificationToken, String userFullName);
    
    /**
     * Send welcome email after successful registration
     */
    void sendWelcomeEmail(String to, String userFullName);
    
    /**
     * Send password change confirmation email
     */
    void sendPasswordChangeConfirmationEmail(String to, String userFullName);
    
    /**
     * Send account activation email
     */
    void sendAccountActivationEmail(String to, String userFullName);
    
    /**
     * Send security alert email
     */
    void sendSecurityAlertEmail(String to, String userFullName, String alertMessage);
    
    /**
     * Send password change notification email
     */
    void sendPasswordChangeNotification(String to, String userFullName);
}
