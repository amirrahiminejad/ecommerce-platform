# 📚 مستندات Iran ECommerce - فهرست ک## 📡 مستندات API

**📁 `03-api-reference/`**

| نام فایل | توضیح | زمان مطالعه | مخاطب |
|----------|--------|-------------|-------|
| **[API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** | مستندات کامل Swagger | 15 دقیقه | API Consumer |
| **[API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md)** | نمونه‌های عملی استفاده از API | 20 دقیقه | Frontend Developer |
| **[SWAGGER_QUICK_GUIDE.md](03-api-reference/SWAGGER_QUICK_GUIDE.md)** | راهنمای سریع Swagger | 10 دقیقه | API Consumer |
| **[EMAIL_SYSTEM_API.md](03-api-reference/EMAIL_SYSTEM_API.md)** | 📧 API سیستم ایمیل و بازیابی رمز عبور | 25 دقیقه | Backend/Frontend Developer |
| **[SETTINGS_API.md](03-api-reference/SETTINGS_API.md)** | ⚙️ API سیستم تنظیمات مدیریت | 20 دقیقه | Backend/Admin Developer | 🗂️ ساختار مستندات

مستندات پروژه Iran ECommerce به صورت منطقی در پوشه‌های جداگانه دسته‌بندی شده‌اند:

```
docs/
├── 01-getting-started/     # 🚀 شروع سریع
├── 02-implementation/      # � پیاده‌سازی
├── 03-api-reference/       # 📡 مرجع API
├── 04-security/           # 🔐 امنیت
├── 05-development/        # 👨‍💻 توسعه
└── postman_collections/   # 📮 کالکشن‌های Postman
```

## �🚀 مستندات شروع سریع

**📁 `01-getting-started/`**

| نام فایل | توضیح | زمان مطالعه | مخاطب |
|----------|--------|-------------|-------|
| **[QUICK_START.md](01-getting-started/QUICK_START.md)** | راه‌اندازی 5 دقیقه‌ای | 10 دقیقه | توسعه‌دهنده |
| **[README.md](01-getting-started/README.md)** | معرفی پروژه و overview کلی | 5 دقیقه | همه |
| **[EMAIL_SYSTEM_USER_GUIDE.md](01-getting-started/EMAIL_SYSTEM_USER_GUIDE.md)** | 📧 راهنمای کاربری بازیابی رمز عبور | 15 دقیقه | کاربر نهایی |

## � مستندات پیاده‌سازی

**📁 `02-implementation/`**

| نام فایل | توضیح | زمان مطالعه | مخاطب |
|----------|--------|-------------|-------|
| **[IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md)** | راهنمای کامل پیاده‌سازی | 30 دقیقه | توسعه‌دهنده ارشد |
| **[EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md)** | سیستم مدیریت خطا و validation | 25 دقیقه | Backend Developer |

## � مستندات API

**📁 `03-api-reference/`**

| نام فایل | توضیح | زمان مطالعه | مخاطب |
|----------|--------|-------------|-------|
| **[API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** | مستندات کامل Swagger | 15 دقیقه | API Consumer |
| **[API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md)** | نمونه‌های عملی استفاده از API | 20 دقیقه | Frontend Developer |
| **[SWAGGER_QUICK_GUIDE.md](03-api-reference/SWAGGER_QUICK_GUIDE.md)** | راهنمای سریع Swagger | 10 دقیقه | API Consumer |

## 🔐 مستندات امنیت

**📁 `04-security/`**

| نام فایل | توضیح | زمان مطالعه | مخاطب |
|----------|--------|-------------|-------|
| **[SECURITY.md](04-security/SECURITY.md)** | راهنمای کامل امنیت | 25 دقیقه | Security Engineer |
| **[SECURITY_CONFIG.md](04-security/SECURITY_CONFIG.md)** | تنظیمات امنیت | 15 دقیقه | DevOps |
| **[SECURITY_QUICK_START.md](04-security/SECURITY_QUICK_START.md)** | راه‌اندازی سریع امنیت | 10 دقیقه | Backend Developer |

## 👨‍� مستندات توسعه

**📁 `05-development/`**

| نام فایل | توضیح | وضعیت |
|----------|--------|-------|
| *(فعلاً خالی)* | مستندات توسعه آتی | 🔄 Coming Soon |

## 📮 کالکشن‌های Postman

**📁 `postman_collections/`**

- `CountryController_Postman_Collection.json`
- `SettingController_Postman_Collection.json`
- `TagController_Postman_Collection.json`

## 🎯 پیشنهاد مطالعه بر اساس نقش

### 👨‍💻 **توسعه‌دهنده جدید:**
1. **[01-getting-started/README.md](01-getting-started/README.md)** (معرفی کلی)
2. **[01-getting-started/QUICK_START.md](01-getting-started/QUICK_START.md)** (راه‌اندازی سریع)
3. **[02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md)** (درک معماری)
4. **[03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md)** (یادگیری عملی)

### 🔧 **Backend Developer:**
1. **[02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md)** (معماری کامل)
2. **[02-implementation/EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md)** (مدیریت خطا)
3. **[04-security/SECURITY.md](04-security/SECURITY.md)** (امنیت)
4. **[03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** (API design)

### 🌐 **Frontend Developer:**
1. **[03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md)** (نمونه‌های integration)
2. **[03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** (endpoint specification)
3. **[02-implementation/EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md)** (error handling)
4. **[01-getting-started/QUICK_START.md](01-getting-started/QUICK_START.md)** (backend setup)

### 🛡️ **Security Engineer:**
1. **[04-security/SECURITY.md](04-security/SECURITY.md)** (security architecture)
2. **[04-security/SECURITY_CONFIG.md](04-security/SECURITY_CONFIG.md)** (security configuration)
3. **[02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md)** (overall architecture)
4. **[03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** (security endpoints)

### 📱 **Mobile Developer:**
1. **[03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md)** (mobile integration examples)
2. **[03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** (API reference)
3. **[02-implementation/EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md)** (error responses)

### 🔍 **QA Engineer:**
1. **[03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md)** (testing endpoints)
2. **[03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md)** (test scenarios)
3. **[02-implementation/EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md)** (error testing)
4. **[02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md)** (system understanding)

## 📊 وضعیت مستندات

| مستند | وضعیت | آخرین به‌روزرسانی | تکمیل |
|-------|-------|------------------|-------|
| 01-getting-started/README.md | ✅ Complete | 2025-09-09 | 100% |
| 01-getting-started/QUICK_START.md | ✅ Complete | 2025-09-09 | 100% |
| 02-implementation/IMPLEMENTATION_GUIDE.md | ✅ Complete | 2025-09-09 | 100% |
| 02-implementation/EXCEPTION_HANDLING.md | ✅ Complete | 2025-09-09 | 100% |
| 03-api-reference/API_DOCUMENTATION.md | ✅ Complete | 2025-09-09 | 100% |
| 03-api-reference/API_USAGE_EXAMPLES.md | ✅ Complete | 2025-09-09 | 100% |
| 03-api-reference/SWAGGER_QUICK_GUIDE.md | ✅ Complete | 2025-09-09 | 100% |
| 03-api-reference/EMAIL_SYSTEM_API.md | ✅ Complete | 2025-10-06 | 100% |
| 03-api-reference/SETTINGS_API.md | ✅ Complete | 2025-10-10 | 100% |
| 01-getting-started/EMAIL_SYSTEM_USER_GUIDE.md | ✅ Complete | 2025-10-06 | 100% |
| 04-security/SECURITY.md | ✅ Complete | 2025-09-07 | 100% |
| 04-security/SECURITY_CONFIG.md | ✅ Complete | 2025-09-07 | 100% |
| 04-security/SECURITY_QUICK_START.md | ✅ Complete | 2025-09-07 | 100% |
| developer/email-system-developer-guide.md | ✅ Complete | 2025-10-06 | 100% |
| 05-development/ | 🔄 Future | - | 0% |

## 🔍 جستجوی سریع مطالب

### **Authentication & JWT:**
- [04-security/SECURITY.md](04-security/SECURITY.md) → JWT Configuration
- [03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md) → Login Examples
- [02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md) → Security Setup

### **Error Handling:**
- [02-implementation/EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md) → Global Exception Handler
- [03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md) → Error Response Examples
- [02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md) → Validation Setup

### **API Integration:**
- [03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md) → Complete Integration Examples
- [03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md) → Endpoint Specifications
- [01-getting-started/QUICK_START.md](01-getting-started/QUICK_START.md) → First API Call

### **Validation:**
- [02-implementation/EXCEPTION_HANDLING.md](02-implementation/EXCEPTION_HANDLING.md) → Custom Validators
- [02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md) → Validation Annotations
- [03-api-reference/API_USAGE_EXAMPLES.md](03-api-reference/API_USAGE_EXAMPLES.md) → Validation Examples

### **Swagger/OpenAPI:**
- [03-api-reference/API_DOCUMENTATION.md](03-api-reference/API_DOCUMENTATION.md) → Swagger Configuration
- [03-api-reference/SWAGGER_QUICK_GUIDE.md](03-api-reference/SWAGGER_QUICK_GUIDE.md) → Quick Swagger Guide
- [02-implementation/IMPLEMENTATION_GUIDE.md](02-implementation/IMPLEMENTATION_GUIDE.md) → OpenAPI Setup

## 📝 نحوه مشارکت در مستندات

### **اضافه کردن مستند جدید:**
1. فایل جدید در پوشه مناسب در `docs/` ایجاد کنید
2. فهرست `DOCUMENTATION_INDEX.md` را به‌روزرسانی کنید
3. لینک در `README.md` اصلی اضافه کنید

### **به‌روزرسانی مستند موجود:**
1. تغییرات لازم را اعمال کنید
2. تاریخ آخرین به‌روزرسانی را تغییر دهید
3. در صورت نیاز، فهرست کلی را به‌روزرسانی کنید

### **استانداردهای نگارش:**
- استفاده از emoji برای بخش‌بندی
- جدول‌بندی اطلاعات مهم
- نمونه‌های عملی برای هر مفهوم
- لینک‌دهی داخلی بین مستندات

### **نامگذاری فولدرها:**
- `01-getting-started/` - مستندات شروع سریع
- `02-implementation/` - پیاده‌سازی و معماری
- `03-api-reference/` - مرجع API و Swagger
- `04-security/` - امنیت و احراز هویت
- `05-development/` - ابزارهای توسعه

## 📞 پشتیبانی مستندات

**سوال دارید؟**
- بررسی این فهرست برای یافتن مستند مرتبط
- جستجو در محتوای مستندات
- در صورت نیاز، موضوع جدیدی در issues ایجاد کنید

## 📈 آمار مستندات

- **تعداد کل مستندات**: 11 فایل
- **تعداد فولدرهای دسته‌بندی**: 5 دسته
- **تعداد کل صفحات**: ~200 صفحه
- **تعداد نمونه‌های کد**: 50+ مثال
- **زبان اصلی**: فارسی + انگلیسی
- **سطح جزئیات**: مبتدی تا پیشرفته

## 🗺️ نقشه راه مستندات

### **آماده:**
- ✅ Getting Started (کامل)
- ✅ Implementation (کامل)
- ✅ API Reference (کامل)
- ✅ Security (کامل)

### **در دست توسعه:**
- 🔄 Development Tools (آینده)
- 🔄 Deployment Guide (آینده)
- 🔄 Performance Optimization (آینده)

---

**🎯 هدف**: ارائه مستندات سازمان‌یافته و کاربردی برای تمام اعضای تیم توسعه

**✅ وضعیت کلی مستندات**: کامل، منظم و آماده استفاده
