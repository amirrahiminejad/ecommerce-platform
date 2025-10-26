# 🚀 Quick Start Guide - Iran ECommerce

## راه‌اندازی 5 دقیقه‌ای

### 📋 **چک‌لیست پیش‌نیازها**
- [ ] Java 17+ نصب شده
- [ ] PostgreSQL نصب و اجرا
- [ ] Maven 3.6+ نصب شده
- [ ] Git دسترسی به repository

### ⚡ **راه‌اندازی سریع**

#### **1. Clone & Setup:**
```bash
# Clone repository
git clone https://gitlab.com/webrayan/iran-bazaar.git
cd iran-store

# ایجاد database
createdb store
```

#### **2. تنظیم اولیه:**
```bash
# کپی تنظیمات نمونه
cp src/main/resources/application.properties.example src/main/resources/application.properties

# ویرایش تنظیمات database
nano src/main/resources/application.properties
```

#### **3. اجرا:**
```bash
# نصب dependencies و اجرا
mvn clean install
mvn spring-boot:run
```

#### **4. تست:**
- Application: http://localhost:8005
- Swagger UI: http://localhost:8005/swagger-ui.html

### 🔑 **اولین Login**

```bash
# دریافت token
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123"}'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 🧪 **تست سریع API**

```bash
# تنظیم token
export TOKEN="YOUR_JWT_TOKEN_HERE"

# دریافت لیست کاربران
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8005/api/acl/users

# ایجاد کاربر جدید
curl -X POST http://localhost:8005/api/acl/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 📚 **مستندات کلیدی**

| مستند | محتوا | لینک |
|--------|-------|------|
| **Implementation Guide** | راهنمای کامل پیاده‌سازی | `docs/IMPLEMENTATION_GUIDE.md` |
| **API Documentation** | مستندات Swagger | `docs/API_DOCUMENTATION.md` |
| **Exception Handling** | سیستم مدیریت خطا | `docs/EXCEPTION_HANDLING.md` |
| **Security Guide** | راهنمای امنیت | `docs/SECURITY_IMPLEMENTATION.md` |

## 🛠️ **Commands مفید**

```bash
# اجرای تست‌ها
mvn test

# Build برای production
mvn clean package

# اجرا در حالت development
mvn spring-boot:run -Dspring.profiles.active=dev

# مشاهده logs
tail -f logs/spring.log
```

## ⚠️ **نکات مهم**

1. **Token Expiry**: JWT tokens در 10 ساعت منقضی می‌شوند
2. **Database**: هر بار restart، schema خودکار update می‌شود
3. **Swagger**: از authentication معاف است
4. **Error Handling**: تمام exceptions خودکار handle می‌شوند

## 🆘 **Support**

**مشکل دارید؟**
- بررسی `logs/spring.log`
- مراجعه به `docs/IMPLEMENTATION_GUIDE.md`
- بررسی `target/surefire-reports/` برای خطاهای test

---
**✅ Ready to go!** پروژه شما آماده توسعه است! 🎉
