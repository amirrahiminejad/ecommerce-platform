# Admin Settings System - README

## 📋 خلاصه سریع

سیستم تنظیمات ادمین برای مدیریت تنظیمات سراسری پلتفرم e-commerce طراحی شده است که امکان مدیریت پیکربندی‌های مختلف سیستم را به مدیران ارائه می‌دهد.

## ✨ ویژگی‌های اصلی

### 🔧 مدیریت تنظیمات
- مشاهده تمام تنظیمات سیستم
- بروزرسانی چندین تنظیمات همزمان
- افزودن تنظیمات جدید
- حذف تنظیمات غیرضروری
- بازگردانی به تنظیمات پیش‌فرض

### 🎨 رابط کاربری
- طراحی responsive با Bootstrap
- انواع input های مختلف بر اساس نوع تنظیمات
- اعتبارسنجی real-time
- پیام‌های واضح برای کاربر
- آیکون‌های Font Awesome

### 🔒 امنیت
- محدودیت دسترسی فقط برای ادمین‌ها
- اعتبارسنجی داده‌ها در سمت سرور
- حفاظت از XSS attacks
- تأیید برای عملیات حساس

## 🗂️ ساختار فایل‌ها

```
admin-settings/
├── controller/
│   └── AdminSettingController.java     # کنترلر اصلی
├── templates/
│   └── admin/settings.html            # صفحه تنظیمات
├── entity/
│   └── Setting.java                   # موجودیت تنظیمات
├── repository/
│   └── SettingRepository.java         # ریپازیتوری
├── service/
│   └── CommonDataInitializer.java     # مقداردهی اولیه
└── docs/
    ├── ADMIN_SETTINGS_GUIDE.md        # راهنمای کامل
    ├── SETTINGS_QUICK_GUIDE.md        # راهنمای سریع
    └── SETTINGS_API.md                # مستندات API
```

## 🚀 نصب و راه‌اندازی

### پیش‌نیازها
- Spring Boot 3.4.4+
- PostgreSQL Database
- دسترسی ادمین

### مراحل راه‌اندازی
1. **بررسی موجودیت Setting:**
   ```java
   // موجودیت Setting در CommonModule موجود است
   @Entity
   public class Setting {
       private String key;
       private String value;
       // ...
   }
   ```

2. **اجرای مقداردهی اولیه:**
   ```bash
   # با اجرای برنامه، CommonDataInitializer اجرا می‌شود
   ./mvnw spring-boot:run
   ```

3. **دسترسی به صفحه تنظیمات:**
   ```
   http://localhost:8080/admin/settings
   ```

## 📖 نحوه استفاده

### 🔍 مشاهده تنظیمات
- به صفحه `/admin/settings` مراجعه کنید
- تمام تنظیمات سیستم نمایش داده می‌شود
- هر تنظیم دارای input مناسب نوعش است

### ✏️ بروزرسانی تنظیمات
1. مقادیر مورد نظر را تغییر دهید
2. دکمه "ذخیره تنظیمات" کلیک کنید
3. پیام تأیید نمایش داده می‌شود

### ➕ افزودن تنظیمات جدید
1. بخش "افزودن تنظیمات جدید" را یابید
2. کلید و مقدار را وارد کنید
3. دکمه "افزودن" کلیک کنید

### 🗑️ حذف تنظیمات
1. روی آیکون سطل آشغال کلیک کنید
2. حذف را تأیید کنید

### 🔄 بازگردانی به پیش‌فرض
1. دکمه "بازگردانی به پیش‌فرض" کلیک کنید
2. عملیات را تأیید کنید

## ⚙️ تنظیمات پیش‌فرض

| کلید | مقدار پیش‌فرض | نوع Input | توضیح |
|------|-------------|-----------|--------|
| `site_name` | فروشگاه اینترنتی | text | نام سایت |
| `site_title` | فروشگاه آنلاین | text | عنوان سایت |
| `site_description` | فروشگاه اینترنتی... | textarea | توضیحات سایت |
| `default_currency` | تومان | text | واحد پول |
| `default_language` | fa | text | زبان پیش‌فرض |
| `admin_email` | admin@online-store.com | email | ایمیل مدیر |
| `support_phone` | +98-21-12345678 | text | تلفن پشتیبانی |
| `max_upload_size` | 10485760 | number | حداکثر سایز آپلود |
| `items_per_page` | 20 | number | تعداد آیتم در صفحه |
| `maintenance_mode` | false | select | حالت نگهداری |

## 🔧 API Endpoints

```http
GET    /admin/settings           # نمایش صفحه تنظیمات
POST   /admin/settings/update    # بروزرسانی تنظیمات
POST   /admin/settings/add       # افزودن تنظیمات
POST   /admin/settings/delete/{id} # حذف تنظیمات
POST   /admin/settings/reset     # بازگردانی پیش‌فرض
```

## 📚 مستندات تکمیلی

### راهنماها
- **[ADMIN_SETTINGS_GUIDE.md](ADMIN_SETTINGS_GUIDE.md)** - راهنمای کامل کاربری
- **[SETTINGS_QUICK_GUIDE.md](SETTINGS_QUICK_GUIDE.md)** - راهنمای سریع
- **[SETTINGS_API.md](../03-api-reference/SETTINGS_API.md)** - مستندات API

### کد منبع
- **AdminSettingController.java** - کنترلر اصلی
- **settings.html** - قالب Thymeleaf
- **CommonDataInitializer.java** - مقداردهی اولیه

## 🐛 عیب‌یابی

### مشکلات رایج

**1. صفحه بارگذاری نمی‌شود**
```bash
# بررسی دسترسی کاربر
SELECT * FROM users WHERE role = 'ADMIN';
```

**2. تنظیمات ذخیره نمی‌شود**
```bash
# بررسی جدول تنظیمات
SELECT * FROM settings WHERE user_id IS NULL;
```

**3. تنظیمات پیش‌فرض نمایش داده نمی‌شود**
```java
// اجرای مجدد مقداردهی
@PostConstruct
public void initializeSettings()
```

### لاگ‌های مفید
```bash
# مشاهده لاگ‌های Spring
tail -f logs/application.log | grep "Setting"
```

## 🤝 مشارکت

### گزارش باگ
1. Issue جدید در GitHub ایجاد کنید
2. توضیح کاملی از مشکل ارائه دهید
3. مراحل بازتولید مشکل را شرح دهید

### درخواست ویژگی
1. ابتدا در Issues جستجو کنید
2. Feature Request جدید ایجاد کنید
3. توضیح کاملی از ویژگی مورد نظر ارائه دهید

## 📊 وضعیت پروژه

- **✅ تکمیل شده:** Core functionality
- **✅ تکمیل شده:** UI/UX design
- **✅ تکمیل شده:** Security measures
- **✅ تکمیل شده:** Documentation
- **🔄 در حال توسعه:** Advanced features
- **📅 برنامه‌ریزی شده:** Mobile responsive enhancements

## 📞 پشتیبانی

**سوالات فنی:**
- بررسی مستندات فوق
- جستجو در GitHub Issues
- ایجاد Issue جدید

**مسائل امنیتی:**
- گزارش مستقیم به تیم امنیت
- عدم انتشار عمومی مسائل امنیتی

---

**نسخه:** 1.0.0  
**آخرین به‌روزرسانی:** اکتبر 2025  
**مجوز:** MIT License
