# 🎨 Thymeleaf Template Structure Guide

## 📁 ساختار فولدر‌بندی ایجاد شده:

```
src/main/resources/
├── templates/                    # 📄 Template های Thymeleaf (.html)
│   ├── admin/                   # 👨‍💼 صفحات مدیریت
│   │   ├── layouts/            # 🔧 Layout های اصلی
│   │   │   ├── main.html      # Layout اصلی ادمین
│   │   │   └── simple.html    # Layout ساده
│   │   ├── fragments/         # 🧩 Fragment های قابل استفاده مجدد
│   │   │   ├── header.html    # Header/Navigation
│   │   │   ├── sidebar.html   # Menu کناری
│   │   │   ├── footer.html    # Footer
│   │   │   └── scripts.html   # JavaScript ها
│   │   └── pages/             # 📋 صفحات اصلی
│   │       ├── dashboard.html
│   │       ├── users.html
│   │       ├── products.html
│   │       └── ...
│   ├── auth/                   # 🔐 صفحات احراز هویت
│   │   ├── login.html
│   │   ├── register.html
│   │   └── forgot-password.html
│   └── error/                  # ❌ صفحات خطا
│       ├── 404.html
│       ├── 500.html
│       └── access-denied.html
└── static/                      # 📦 فایل‌های استاتیک
    └── admin/                   # 🎨 منابع ادمین
        ├── css/                # 🎨 فایل‌های CSS
        ├── js/                 # ⚡ فایل‌های JavaScript
        ├── images/             # 🖼️ تصاویر
        ├── fonts/              # 🔤 فونت‌ها
        └── vendors/            # 📚 کتابخانه‌های Third-party
```

## 🚀 کجا قالبت رو قرار بدی:

### 📥 **مرحله 1: استخراج قالب**
```bash
# قالب HTML رو در این مسیر قرار بده:
iran-commerce/temp-template/
├── profile.html
├── css/
├── js/
├── images/
├── fonts/
└── ...
```

### 📋 **مرحله 2: توزیع فایل‌ها**

#### 🎨 **CSS Files:**
```bash
# از قالب:
temp-template/css/* 
# به:
src/main/resources/static/admin/css/
```

#### ⚡ **JavaScript Files:**
```bash
# از قالب:
temp-template/js/*
# به:
src/main/resources/static/admin/js/
```

#### 🖼️ **Images:**
```bash
# از قالب:
temp-template/images/*
# به:
src/main/resources/static/admin/images/
```

#### 🔤 **Fonts:**
```bash
# از قالب:
temp-template/fonts/*
# به:
src/main/resources/static/admin/fonts/
```

#### 📚 **Vendor Libraries (Bootstrap, jQuery, etc.):**
```bash
# از قالب:
temp-template/vendors/*
# به:
src/main/resources/static/admin/vendors/
```

### 📄 **مرحله 3: HTML Templates**

#### 🏠 **صفحه اصلی (index.html):**
```bash
# از قالب:
temp-template/profile.html
# تبدیل به:
src/main/resources/templates/admin/pages/dashboard.html
```

#### 📋 **سایر صفحات:**
```bash
# هر صفحه HTML از قالب:
temp-template/pages/users.html
# تبدیل به:
src/main/resources/templates/admin/pages/users.html
```

## 🔧 **مثال ساختار Layout:**

### **main.html** (Layout اصلی):
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${pageTitle}">Admin Dashboard</title>
    <!-- CSS Files -->
</head>
<body>
    <!-- Header -->
    <div th:replace="~{admin/fragments/header :: header}"></div>
    
    <!-- Sidebar -->
    <div th:replace="~{admin/fragments/sidebar :: sidebar}"></div>
    
    <!-- Content -->
    <main>
        <div th:insert="~{${content}}"></div>
    </main>
    
    <!-- Footer -->
    <div th:replace="~{admin/fragments/footer :: footer}"></div>
    
    <!-- Scripts -->
    <div th:replace="~{admin/fragments/scripts :: scripts}"></div>
</body>
</html>
```

## 📝 **بعد از قرار دادن قالب:**

1. **بگو کدوم صفحه رو اول می‌خوای**: Dashboard, Users, Products, etc.
2. **اولویت‌بندی کن**: کدوم صفحات مهم‌ترن
3. **API Integration**: کدوم API ها رو باید متصل کنم
4. **Security**: کدوم صفحات نیاز به مجوز خاص دارن

## 🎯 **آماده برای شروع:**

بعد از قرار دادن قالب، بهم بگو:
- ✅ **فایل‌ها کپی شدن**
- 📋 **لیست صفحاتی که می‌خوای**
- 🔧 **کدوم قسمت رو اول شروع کنیم**

**الان قالبت رو قرار بده و شروع کنیم! 🚀**
