# 📚 مستندات پروژه Iran ECommerce

این پوشه شامل تمام مستندات فنی و راهنماهای استفاده از پروژه Iran ECommerce است.

## 📋 فهرست مستندات

### 🔒 امنیت (Security)
- **[SECURITY.md](./SECURITY.md)** - مستندات کامل سیستم امنیت
- **[SECURITY_QUICK_START.md](./SECURITY_QUICK_START.md)** - راهنمای سریع امنیت
- **[SECURITY_CONFIG.md](./SECURITY_CONFIG.md)** - پیکربندی‌های امنیتی

### 🔧 API
- **[postman_collections/](./postman_collections/)** - Collection های Postman برای تست API ها

## 🚀 شروع سریع

### 1. راه‌اندازی امنیت
```bash
# خواندن راهنمای سریع امنیت
cat docs/SECURITY_QUICK_START.md

# تست با Postman
# Import کردن فایل‌های موجود در postman_collections/
```

### 2. تست API ها
```bash
# ورود و دریافت Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123"}'

# استفاده از Token
curl -X GET http://localhost:8080/api/acl/users \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 📖 نحوه استفاده از مستندات

### برای توسعه‌دهندگان
1. ابتدا `SECURITY_QUICK_START.md` را بخوانید
2. برای پیاده‌سازی ویژگی‌های جدید، `SECURITY.md` را مطالعه کنید
3. برای تنظیمات محیط تولید، `SECURITY_CONFIG.md` را ببینید

### برای تست‌کنندگان
1. از Postman Collections استفاده کنید
2. نمونه‌های موجود در `SECURITY_QUICK_START.md` را امتحان کنید

### برای مدیران سیستم
1. `SECURITY_CONFIG.md` برای تنظیمات سرور
2. بخش Security Checklist برای بررسی امنیت

## 🔄 به‌روزرسانی مستندات

این مستندات همراه با کد به‌روزرسانی می‌شوند. در صورت تغییرات مهم در سیستم امنیت، لطفاً مستندات مربوطه را نیز به‌روزرسانی کنید.

### قوانین نگارش
- از زبان فارسی برای توضیحات استفاده کنید
- کدها و مثال‌ها به انگلیسی باشند
- از emoji برای بهتر شدن خوانایی استفاده کنید
- مثال‌های عملی ارائه دهید

## 📞 پشتیبانی

برای سوالات فنی:
- ایجاد Issue در مخزن پروژه
- مراجعه به مستندات Spring Security
- بررسی لاگ‌های امنیتی سیستم

---

*نکته: این مستندات برای نسخه فعلی پروژه Iran ECommerce نوشته شده است و ممکن است با نسخه‌های آینده تغییر کند.*
