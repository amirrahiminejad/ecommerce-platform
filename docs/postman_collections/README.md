# 📮 Postman Collections

## 📋 محتویات

این پوشه شامل کالکشن‌های Postman برای تست API های مختلف پروژه است:

### 📄 کالکشن‌های موجود:

#### **🔐 Authentication & User Management:**
- **[AuthController_Postman_Collection.json](AuthController_Postman_Collection.json)** - احراز هویت (Login, Signup, Password)
- **[UserController_Postman_Collection.json](UserController_Postman_Collection.json)** - مدیریت کاربران (CRUD, Roles, Permissions)

#### **📢 Advertisement Management:**
- **[AdController_Postman_Collection.json](AdController_Postman_Collection.json)** - مدیریت آگهی‌ها (Create, Read, Update, Delete)

#### **🌍 Core Data Management:**
- **[CountryController_Postman_Collection.json](CountryController_Postman_Collection.json)** - مدیریت کشورها
- **[LocationController_Postman_Collection.json](LocationController_Postman_Collection.json)** - مدیریت مکان‌ها
- **[TagController_Postman_Collection.json](TagController_Postman_Collection.json)** - مدیریت تگ‌ها
- **[SettingController_Postman_Collection.json](SettingController_Postman_Collection.json)** - مدیریت تنظیمات

#### **⚙️ Environment:**
- **[Iran_Bazaar_Environment.postman_environment.json](Iran_Bazaar_Environment.postman_environment.json)** - متغیرهای محیطی

## 🚀 نحوه استفاده

### 1. Import Collections در Postman:
```
1. Postman را باز کنید
2. روی "Import" کلیک کنید
3. فایل‌های JSON مورد نظر را انتخاب کنید
4. کالکشن‌ها import خواهند شد
```

### 2. Import Environment:
```
1. در Postman روی "Environments" کلیک کنید
2. "Import" را انتخاب کنید
3. فایل Iran_Bazaar_Environment.postman_environment.json را import کنید
4. Environment را فعال کنید
```

### 3. دریافت JWT Token:
```
از کالکشن AuthController:
- "Login User" را اجرا کنید
- Token به طور خودکار در environment ذخیره می‌شود
- در سایر requestها از {{jwt_token}} استفاده کنید
```

## 📋 Environment Variables

| Variable | Value | توضیح |
|----------|-------|--------|
| `baseUrl` | http://localhost:8005 | آدرس پایه API |
| `jwt_token` | (auto-set) | توکن احراز هویت |
| `username` | admin | نام کاربری پیش‌فرض |
| `password` | 123 | رمز عبور پیش‌فرض |
| `userId` | 1 | شناسه کاربر برای تست |
| `adId` | 1 | شناسه آگهی برای تست |
| `locationId` | 1 | شناسه مکان برای تست |
| `countryId` | 1 | شناسه کشور برای تست |
| `tagId` | 1 | شناسه تگ برای تست |

## 🔗 پیشنهاد ترتیب تست

### 🎯 **مسیر پیشنهادی برای تست کامل:**

1. **🔐 Authentication:**
   - AuthController → Login User
   - AuthController → Signup New User

2. **👥 User Management:**
   - UserController → Get All Users
   - UserController → Create New User
   - UserController → Assign Roles

3. **📢 Advertisement:**
   - AdController → Create New Ad
   - AdController → Get All Ads
   - AdController → Search Ads

4. **🌍 Core Data:**
   - LocationController → Get All Locations
   - CountryController → Get All Countries
   - TagController → Get All Tags

## 🎯 کاربردها

- 🧪 **Manual Testing** - تست دستی API ها
- 🔄 **Automated Testing** - تست خودکار
- 📊 **Load Testing** - تست بار
- 🐛 **Debugging** - debug کردن مشکلات
- 📝 **Documentation** - مستندسازی API
- 🎓 **Learning** - یادگیری نحوه استفاده از API

## 🔧 Features خاص

### **AuthController Collection:**
- 🔄 Auto-save JWT token پس از login
- 🧪 Multiple login scenarios
- ⚡ Test scripts برای validation

### **UserController Collection:**
- 👥 Complete CRUD operations
- 🔐 Role management
- 🔑 Password management
- ✅ Permission checking

### **AdController Collection:**
- 📢 Advertisement CRUD
- 🔍 Advanced search filters  
- 👤 User-specific ads
- 🏷️ Category filtering

## 📋 نکات مهم

- ⚠️ **همیشه JWT token معتبر استفاده کنید**
- 🔒 **در production از credentials امن استفاده کنید**
- 📊 **نتایج تست‌ها را document کنید**
- 🔄 **کالکشن‌ها را با تغییرات API به‌روزرسانی کنید**
- 🧪 **قبل از تست، server را راه‌اندازی کنید**

## 🆘 Troubleshooting

### ❌ **مشکلات رایج:**

**401 Unauthorized:**
```
- بررسی کنید JWT token معتبر باشد
- دوباره login کنید
- Environment variables را check کنید
```

**404 Not Found:**
```
- baseUrl را بررسی کنید
- مطمئن شوید server اجرا شده
- endpoint path را check کنید
```

**500 Internal Server Error:**
```
- Server logs را بررسی کنید
- Database connection را check کنید
- Request body format را بررسی کنید
```
