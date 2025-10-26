# 📧 Email System & Password Reset API Documentation

## Overview
سیستم ایمیل و بازیابی رمز عبور Iran ECommerce که امکان ارسال انواع ایمیل‌ها و فرایند امن بازیابی رمز عبور را فراهم می‌کند.

## Features
- ✅ ارسال ایمیل با قالب‌های HTML زیبا
- 🔐 سیستم امن بازیابی رمز عبور
- ⏰ Token های موقت با انقضای 15 دقیقه‌ای
- 🛡️ محدودیت نرخ درخواست (Rate Limiting)
- 📱 Responsive Web Interface
- 🔍 RESTful API کامل

---

## 🔗 API Endpoints

### 1. درخواست بازیابی رمز عبور

**POST** `/api/auth/password-reset/request`

ارسال ایمیل بازیابی رمz عبور به کاربر

#### Request Parameters:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `email` | string | Yes | آدرس ایمیل کاربر |

#### Example Request:
```bash
curl -X POST "http://localhost:8005/api/auth/password-reset/request" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "email=user@example.com"
```

#### Success Response (200):
```json
{
  "success": true,
  "message": "لینک بازیابی رمز عبور به ایمیل شما ارسال شد."
}
```

#### Error Responses:
```json
// Rate limit exceeded (429)
{
  "success": false,
  "message": "تعداد درخواست‌های بازیابی رمز عبور بیش از حد مجاز است"
}

// Validation error (400)
{
  "success": false,
  "message": "ایمیل معتبر وارد کنید"
}
```

---

### 2. اعتبارسنجی توکن بازیابی

**GET** `/api/auth/password-reset/validate`

بررسی معتبر بودن توکن بازیابی رمز عبور

#### Request Parameters:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `token` | string | Yes | توکن بازیابی |

#### Example Request:
```bash
curl -X GET "http://localhost:8005/api/auth/password-reset/validate?token=ABC123..."
```

#### Success Response (200):
```json
// Valid token
{
  "success": true,
  "valid": true,
  "message": "توکن معتبر است",
  "user": {
    "firstName": "علی",
    "lastName": "احمدی",
    "email": "ali@example.com"
  }
}

// Invalid token
{
  "success": true,
  "valid": false,
  "message": "توکن نامعتبر یا منقضی شده"
}
```

---

### 3. تنظیم رمز عبور جدید

**POST** `/api/auth/password-reset/reset`

تنظیم رمز عبور جدید با استفاده از توکن بازیابی

#### Request Parameters:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `token` | string | Yes | توکن بازیابی |
| `newPassword` | string | Yes | رمز عبور جدید (حداقل 6 کاراکتر) |
| `confirmPassword` | string | Yes | تکرار رمز عبور جدید |

#### Example Request:
```bash
curl -X POST "http://localhost:8005/api/auth/password-reset/reset" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "token=ABC123...&newPassword=newPass123&confirmPassword=newPass123"
```

#### Success Response (200):
```json
{
  "success": true,
  "message": "رمز عبور شما با موفقیت تغییر کرد."
}
```

#### Error Responses:
```json
// Password mismatch (400)
{
  "success": false,
  "message": "رمز عبور و تکرار آن یکسان نیست"
}

// Invalid token (400)
{
  "success": false,
  "message": "لینک بازیابی رمز عبور نامعتبر یا منقضی شده است."
}
```

---

## 🌐 Web Interface

### صفحات موجود:

1. **فراموشی رمز عبور**: `/auth/forgot-password`
2. **تنظیم رمز جدید**: `/auth/reset-password?token=...`

### ویژگی‌های UI:
- 📱 طراحی Responsive
- 🎨 UI زیبا با Bootstrap 5
- ⚡ نمایش قدرت رمز عبور
- 👁️ امکان نمایش/مخفی کردن رمز
- ✅ اعتبارسنجی Real-time

---

## ⚙️ Configuration

### Email Settings (application.properties):

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Application URLs
app.base-url=${BASE_URL:http://localhost:8005}
app.frontend-url=${FRONTEND_URL:http://localhost:8005}

# Password Reset Configuration
app.password-reset.token-expiration=900000
app.password-reset.max-attempts=3
```

### Environment Variables:
```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
BASE_URL=https://iran-bazaar.com
FRONTEND_URL=https://iran-bazaar.com
```

---

## 🔒 Security Features

### Token Security:
- **256-bit** cryptographically secure tokens
- **15-minute** expiration time
- **One-time use** tokens
- **Rate limiting**: 3 attempts per hour

### Email Security:
- **No sensitive data** in email content
- **HTTPS links** in production
- **IP logging** for security monitoring

### Additional Security:
- **Password strength** validation
- **Brute force protection**
- **Audit logging**

---

## 📊 Database Schema

### PasswordResetToken Entity:
```sql
CREATE TABLE acl_password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES acl_users(id),
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    ip_address VARCHAR(45),
    user_agent TEXT
);
```

---

## 🎯 Usage Examples

### Frontend Integration (JavaScript):

```javascript
// Request password reset
async function requestPasswordReset(email) {
    const response = await fetch('/api/auth/password-reset/request', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `email=${encodeURIComponent(email)}`
    });
    
    const result = await response.json();
    return result;
}

// Validate token
async function validateResetToken(token) {
    const response = await fetch(`/api/auth/password-reset/validate?token=${encodeURIComponent(token)}`);
    const result = await response.json();
    return result;
}

// Reset password
async function resetPassword(token, newPassword, confirmPassword) {
    const response = await fetch('/api/auth/password-reset/reset', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `token=${encodeURIComponent(token)}&newPassword=${encodeURIComponent(newPassword)}&confirmPassword=${encodeURIComponent(confirmPassword)}`
    });
    
    const result = await response.json();
    return result;
}
```

---

## 🧪 Testing

### Manual Testing:
1. Go to `/auth/forgot-password`
2. Enter valid email address
3. Check email for reset link
4. Click link and set new password
5. Login with new password

### API Testing with Postman:
Import the provided Postman collection for complete API testing.

---

## 🐛 Troubleshooting

### Common Issues:

1. **Email not received**:
   - Check spam folder
   - Verify SMTP credentials
   - Check application logs

2. **Token expired**:
   - Request new reset link
   - Tokens expire after 15 minutes

3. **Rate limit exceeded**:
   - Wait 1 hour before next attempt
   - Contact support if needed

### Debug Logging:
```properties
logging.level.com.webrayan.commerce.modules.acl.service.impl.PasswordResetServiceImpl=DEBUG
logging.level.com.webrayan.commerce.core.service.impl.EmailServiceImpl=DEBUG
```

---

## 📈 Monitoring

### Metrics to Track:
- Password reset requests per hour
- Email delivery success rate
- Token usage statistics
- Failed authentication attempts

### Log Messages:
- Password reset initiated
- Email sent successfully
- Token validation attempts
- Password changed successfully

---

## 🔄 Future Enhancements

- [ ] SMS-based password reset
- [ ] Multi-factor authentication
- [ ] Custom email templates
- [ ] Password history validation
- [ ] Account lockout policies
- [ ] Real-time notifications

---

**Last Updated**: October 2025  
**Version**: 1.0.0
