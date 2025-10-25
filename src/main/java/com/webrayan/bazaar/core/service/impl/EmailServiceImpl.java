package com.webrayan.bazaar.core.service.impl;

import com.webrayan.bazaar.core.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService using Spring Mail
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender emailSender;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@iran-bazaar.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            emailSender.send(message);
            log.info("ایمیل ساده به {} ارسال شد", to);
            
        } catch (Exception e) {
            log.error("خطا در ارسال ایمیل ساده به {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("خطا در ارسال ایمیل", e);
        }
    }
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("noreply@iran-bazaar.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            emailSender.send(message);
            log.info("ایمیل HTML به {} ارسال شد", to);
            
        } catch (Exception e) {
            log.error("خطا در ارسال ایمیل HTML به {}: {}", to, e.getMessage(), e);
            throw new MessagingException("خطا در ارسال ایمیل HTML", e);
        }
    }
    
    @Override
    public void sendPasswordResetEmail(String to, String resetToken, String userFullName) {
        String subject = "بازیابی رمز عبور - پرشیا بازار";
        String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;
        
        String htmlContent = buildPasswordResetEmailTemplate(userFullName, resetUrl);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل بازیابی رمز عبور به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل بازیابی رمز عبور به {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("خطا در ارسال ایمیل بازیابی رمز عبور", e);
        }
    }
    
    @Override
    public void sendEmailVerificationEmail(String to, String verificationToken, String userFullName) {
        String subject = "تأیید ایمیل - پرشیا بازار";
        String verificationUrl = frontendUrl + "/auth/verify-email?token=" + verificationToken;
        
        String htmlContent = buildEmailVerificationTemplate(userFullName, verificationUrl);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل تأیید به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل تأیید به {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("خطا در ارسال ایمیل تأیید", e);
        }
    }
    
    @Override
    public void sendWelcomeEmail(String to, String userFullName) {
        String subject = "خوش آمدید به پرشیا بازار";
        String htmlContent = buildWelcomeEmailTemplate(userFullName);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل خوش‌آمدگویی به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل خوش‌آمدگویی به {}: {}", to, e.getMessage(), e);
            // Don't throw exception for welcome emails
        }
    }
    
    @Override
    public void sendPasswordChangeConfirmationEmail(String to, String userFullName) {
        String subject = "تغییر رمز عبور - پرشیا بازار";
        String htmlContent = buildPasswordChangeConfirmationTemplate(userFullName);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل تأیید تغییر رمز به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل تأیید تغییر رمز به {}: {}", to, e.getMessage(), e);
            // Don't throw exception for confirmation emails
        }
    }
    
    @Override
    public void sendAccountActivationEmail(String to, String userFullName) {
        String subject = "فعال‌سازی حساب کاربری - پرشیا بازار";
        String htmlContent = buildAccountActivationTemplate(userFullName);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل فعال‌سازی حساب به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل فعال‌سازی حساب به {}: {}", to, e.getMessage(), e);
            // Don't throw exception for activation emails
        }
    }
    
    @Override
    public void sendSecurityAlertEmail(String to, String userFullName, String alertMessage) {
        String subject = "هشدار امنیتی - پرشیا بازار";
        String htmlContent = buildSecurityAlertTemplate(userFullName, alertMessage);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل هشدار امنیتی به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل هشدار امنیتی به {}: {}", to, e.getMessage(), e);
            // Don't throw exception for security alert emails
        }
    }
    
    @Override
    public void sendPasswordChangeNotification(String to, String userFullName) {
        String subject = "اطلاع رسانی تغییر رمز عبور - پرشیا بازار";
        String htmlContent = buildPasswordChangeNotificationTemplate(userFullName);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            log.info("ایمیل اطلاع رسانی تغییر رمز به {} ارسال شد", to);
        } catch (MessagingException e) {
            log.error("خطا در ارسال ایمیل اطلاع رسانی تغییر رمز به {}: {}", to, e.getMessage(), e);
            // Don't throw exception for notification emails
        }
    }
    
    // Email Template Builders
    
    private String buildPasswordResetEmailTemplate(String userFullName, String resetUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>بازیابی رمز عبور</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .title { font-size: 20px; color: #333; margin-bottom: 20px; }
                    .content { margin-bottom: 30px; }
                    .button { display: inline-block; background-color: #dc3545; color: white; text-decoration: none; padding: 12px 30px; border-radius: 5px; font-weight: bold; margin: 20px 0; }
                    .button:hover { background-color: #c82333; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                        <div class="title">بازیابی رمز عبور</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <p>درخواست بازیابی رمز عبور برای حساب کاربری شما دریافت شد. برای تنظیم رمز عبور جدید، روی دکمه زیر کلیک کنید:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">بازیابی رمز عبور</a>
                        </div>
                        
                        <div class="warning">
                            <strong>توجه:</strong> این لینک تنها به مدت 15 دقیقه معتبر است. اگر درخواست بازیابی رمز عبور را نداده‌اید، این ایمیل را نادیده بگیرید.
                        </div>
                        
                        <p>اگر دکمه بالا کار نمی‌کند، لینک زیر را کپی کرده و در مرورگر خود وارد کنید:</p>
                        <p style="word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 3px; font-family: monospace;">%s</p>
                    </div>
                    
                    <div class="footer">
                        <p>این ایمیل از طرف سیستم پرشیا بازار ارسال شده است.</p>
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName, resetUrl, resetUrl);
    }
    
    private String buildEmailVerificationTemplate(String userFullName, String verificationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>تأیید ایمیل</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .title { font-size: 20px; color: #333; margin-bottom: 20px; }
                    .content { margin-bottom: 30px; }
                    .button { display: inline-block; background-color: #28a745; color: white; text-decoration: none; padding: 12px 30px; border-radius: 5px; font-weight: bold; margin: 20px 0; }
                    .button:hover { background-color: #218838; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                        <div class="title">تأیید ایمیل</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <p>برای تکمیل فرآیند ثبت نام، لطفاً ایمیل خود را تأیید کنید:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">تأیید ایمیل</a>
                        </div>
                        
                        <p>اگر دکمه بالا کار نمی‌کند، لینک زیر را کپی کرده و در مرورگر خود وارد کنید:</p>
                        <p style="word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 3px; font-family: monospace;">%s</p>
                    </div>
                    
                    <div class="footer">
                        <p>این ایمیل از طرف سیستم پرشیا بازار ارسال شده است.</p>
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName, verificationUrl, verificationUrl);
    }
    
    private String buildWelcomeEmailTemplate(String userFullName) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>خوش آمدید</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .title { font-size: 20px; color: #333; margin-bottom: 20px; }
                    .content { margin-bottom: 30px; }
                    .button { display: inline-block; background-color: #007bff; color: white; text-decoration: none; padding: 12px 30px; border-radius: 5px; font-weight: bold; margin: 20px 0; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                        <div class="title">خوش آمدید!</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <p>به خانواده بزرگ پرشیا بازار خوش آمدید! 🎉</p>
                        
                        <p>حساب کاربری شما با موفقیت ایجاد شد. حالا می‌توانید:</p>
                        <ul>
                            <li>آگهی‌های خود را ثبت کنید</li>
                            <li>در میان هزاران آگهی جستجو کنید</li>
                            <li>با فروشندگان ارتباط برقرار کنید</li>
                            <li>پروفایل خود را مدیریت کنید</li>
                        </ul>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">ورود به حساب کاربری</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>از اینکه پرشیا بازار را انتخاب کردید، متشکریم!</p>
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName, frontendUrl + "/profile");
    }
    
    private String buildPasswordChangeConfirmationTemplate(String userFullName) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <title>تغییر رمز عبور</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .success { background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <div class="success">
                            ✅ رمز عبور شما با موفقیت تغییر کرد.
                        </div>
                        
                        <p>اگر این تغییر را خودتان انجام نداده‌اید، لطفاً فوراً با پشتیبانی تماس بگیرید.</p>
                    </div>
                    
                    <div class="footer">
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName);
    }
    
    private String buildAccountActivationTemplate(String userFullName) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <title>فعال‌سازی حساب</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .success { background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <div class="success">
                            ✅ حساب کاربری شما فعال شد!
                        </div>
                        
                        <p>اکنون می‌توانید از تمامی امکانات پرشیا بازار استفاده کنید.</p>
                    </div>
                    
                    <div class="footer">
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName);
    }
    
    private String buildSecurityAlertTemplate(String userFullName, String alertMessage) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <title>هشدار امنیتی</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .alert { background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <div class="alert">
                            ⚠️ <strong>هشدار امنیتی:</strong> %s
                        </div>
                        
                        <p>اگر این فعالیت را خودتان انجام نداده‌اید، لطفاً فوراً رمز عبور خود را تغییر دهید.</p>
                    </div>
                    
                    <div class="footer">
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName, alertMessage);
    }
    
    private String buildPasswordChangeNotificationTemplate(String userFullName) {
        return String.format("""
            <!DOCTYPE html>
            <html dir="rtl" lang="fa">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>اطلاع رسانی تغییر رمز عبور</title>
                <style>
                    body { font-family: Tahoma, Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 24px; font-weight: bold; color: #2c5aa0; margin-bottom: 10px; }
                    .title { font-size: 20px; color: #333; margin-bottom: 20px; }
                    .content { margin-bottom: 30px; }
                    .success { background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .footer { font-size: 12px; color: #666; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">پرشیا بازار</div>
                        <div class="title">اطلاع رسانی تغییر رمز عبور</div>
                    </div>
                    
                    <div class="content">
                        <p>سلام <strong>%s</strong> عزیز،</p>
                        
                        <div class="success">
                            ✅ <strong>موفق:</strong> رمز عبور حساب کاربری شما با موفقیت تغییر یافت.
                        </div>
                        
                        <p><strong>جزئیات:</strong></p>
                        <ul>
                            <li>زمان تغییر: %s</li>
                            <li>حساب کاربری: %s</li>
                        </ul>
                        
                        <div class="warning">
                            <strong>توجه:</strong> اگر این تغییر را خودتان انجام نداده‌اید، فوراً با پشتیبانی تماس بگیرید و حساب کاربری خود را بررسی کنید.
                        </div>
                        
                        <p>برای امنیت بیشتر حساب کاربری خود، توصیه می‌کنیم:</p>
                        <ul>
                            <li>از رمز عبور قوی و پیچیده استفاده کنید</li>
                            <li>رمز عبور خود را به اشتراک نگذارید</li>
                            <li>از شبکه‌های وای‌فای عمومی برای ورود به حساب خود استفاده نکنید</li>
                        </ul>
                    </div>
                    
                    <div class="footer">
                        <p>این ایمیل از طرف سیستم پرشیا بازار ارسال شده است.</p>
                        <p>© 2025 پرشیا بازار. تمامی حقوق محفوظ است.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userFullName, 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                userFullName);
    }
}
