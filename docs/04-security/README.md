# 🔐 Security - امنیت

## 📋 محتویات این بخش

این بخش شامل مستندات کامل امنیت پروژه Persia Bazaar است:

### 📄 فایل‌های موجود:

- **[SECURITY.md](SECURITY.md)** - راهنمای کامل امنیت، JWT، RBAC و security architecture
- **[SECURITY_CONFIG.md](SECURITY_CONFIG.md)** - تنظیمات و کانفیگوریشن امنیت
- **[SECURITY_QUICK_START.md](SECURITY_QUICK_START.md)** - راه‌اندازی سریع امنیت

## 🎯 مخاطب

- 🛡️ Security Engineers
- 🔧 Backend Developers
- ⚙️ DevOps Engineers
- 🏗️ System Administrators
- 🔍 Security Auditors

## ⏱️ زمان مطالعه

- **مجموع**: 50 دقیقه
- **SECURITY.md**: 25 دقیقه
- **SECURITY_CONFIG.md**: 15 دقیقه
- **SECURITY_QUICK_START.md**: 10 دقیقه

## 🔗 مسیر پیشنهادی

### 🛡️ برای Security Engineers:
1. **SECURITY.md** - درک کامل security architecture
2. **SECURITY_CONFIG.md** - جزئیات تنظیمات
3. **SECURITY_QUICK_START.md** - راه‌اندازی سریع

### 🔧 برای Backend Developers:
1. **SECURITY_QUICK_START.md** - شروع سریع
2. **SECURITY.md** - درک عمیق JWT و RBAC
3. **SECURITY_CONFIG.md** - configuration details

### ⚙️ برای DevOps:
1. **SECURITY_CONFIG.md** - deployment configurations
2. **SECURITY.md** - security requirements
3. **SECURITY_QUICK_START.md** - setup guide

## 📚 موضوعات پوشش داده شده

### در SECURITY.md:
- 🔑 JWT Authentication Implementation
- 👥 Role-Based Access Control (RBAC)
- 🛡️ Method-Level Security
- 🔒 Custom Permission Evaluator
- 💾 BCrypt Password Encoding
- 📊 User Status Management

### در SECURITY_CONFIG.md:
- ⚙️ SecurityConfig Class
- 🔧 JWT Configuration
- 🌐 CORS Settings
- 📋 Endpoint Security Rules
- 🔐 Authentication Provider Setup

### در SECURITY_QUICK_START.md:
- 🚀 Quick Setup Steps
- 🔑 Default Credentials
- 🧪 Testing Security
- 💡 Common Issues

## 🔑 اطلاعات امنیتی مهم

### Default Credentials:
- **Username**: admin
- **Password**: 123
- **Role**: SYSTEM_ADMIN

### JWT Configuration:
- **Expiration**: 10 ساعت
- **Algorithm**: HS256
- **Secret**: قابل تنظیم در application.properties

### Security Features:
- ✅ JWT-based Authentication
- ✅ Role-based Authorization
- ✅ Method-level Security
- ✅ Custom Permission Evaluator
- ✅ Password Encryption (BCrypt)
- ✅ CORS Configuration

## ⚠️ نکات امنیتی

- 🔒 همیشه JWT secret را در production تغییر دهید
- 🔑 از passwords قوی استفاده کنید
- 🛡️ HTTPS را در production فعال کنید
- 📊 User status را به‌روز نگه دارید
- 🔍 Security logs را monitor کنید

## ➡️ بعد از اینجا کجا برویم؟

بعد از تکمیل این بخش:

- **برای API Usage**: [../03-api-reference/API_USAGE_EXAMPLES.md](../03-api-reference/API_USAGE_EXAMPLES.md)
- **برای Implementation**: [../02-implementation/IMPLEMENTATION_GUIDE.md](../02-implementation/IMPLEMENTATION_GUIDE.md)
- **برای Exception Handling**: [../02-implementation/EXCEPTION_HANDLING.md](../02-implementation/EXCEPTION_HANDLING.md)

---
**🛡️ برای درک کامل امنیت، از SECURITY.md شروع کنید!**
