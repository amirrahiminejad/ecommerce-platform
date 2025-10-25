# استایل‌های یکسان Persia Bazaar

این پروژه شامل سیستم طراحی یکسان و جامع برای تمام صفحات وب‌سایت Persia Bazaar است تا تجربه کاربری یکدست و حرفه‌ای ارائه دهد.

## 🎨 ویژگی‌های اصلی

### ✅ **دکمه‌های یکسان**
- استایل یکدست برای تمام دکمه‌ها
- افکت‌های انیمیشن زیبا (hover, focus, active)
- اندازه‌های مختلف (sm, normal, lg)
- انواع رنگی (primary, secondary, success, danger, warning)
- دکمه‌های outline و solid
- افکت نوری shimmer برای دکمه‌های اصلی

### 📋 **کارت‌های یکسان**
- طراحی یکدست برای تمام کارت‌ها
- سایه‌های نرم و حرفه‌ای
- افکت hover برای تعامل بهتر
- هدر و فوتر استاندارد

### 📄 **فرم‌های یکسان**
- استایل یکدست برای input ها
- Label های زیبا و خوانا
- validation styling
- فاصله‌گذاری صحیح
- focus effects

### 🚨 **پیام‌های هشدار یکسان**
- طراحی یکدست برای alert ها
- انواع مختلف (success, error, warning, info)
- آیکون‌های مناسب
- انیمیشن slideInDown

### 🗂️ **هدر صفحات یکسان**
- طراحی gradient زیبا
- افکت floating animation
- typography یکدست
- responsive design

### 🍞 **Breadcrumb یکسان**
- طراحی مدرن و تمیز
- separator های زیبا
- hover effects

## 🎯 کلاس‌های اصلی

### دکمه‌ها
```html
<!-- دکمه‌های اصلی -->
<button class="btn-universal btn-primary">
    <i class="bi bi-check-lg"></i>
    تایید
</button>

<button class="btn-universal btn-secondary">
    <i class="bi bi-x-lg"></i>
    انصراف
</button>

<!-- دکمه‌های outline -->
<button class="btn-universal btn-outline-primary">مشاهده</button>

<!-- اندازه‌های مختلف -->
<button class="btn-universal btn-primary btn-sm">کوچک</button>
<button class="btn-universal btn-primary">معمولی</button>
<button class="btn-universal btn-primary btn-lg">بزرگ</button>
```

### کارت‌ها
```html
<div class="card-universal">
    <div class="card-header">
        <i class="bi bi-person me-2"></i>
        عنوان کارت
    </div>
    <div class="card-body">
        محتوای کارت
    </div>
</div>
```

### فرم‌ها
```html
<form class="form-universal">
    <div class="form-group">
        <label class="form-label">نام *</label>
        <input type="text" class="form-control" placeholder="نام خود را وارد کنید">
    </div>
    
    <div class="d-flex justify-content-between pt-4 mt-4 border-top">
        <button type="submit" class="btn-universal btn-primary">
            <i class="bi bi-check-lg"></i>
            ذخیره
        </button>
        <button type="button" class="btn-universal btn-outline-secondary">
            <i class="bi bi-x-lg"></i>
            انصراف
        </button>
    </div>
</form>
```

### پیام‌های هشدار
```html
<div class="alert-universal alert-success">
    <i class="bi bi-check-circle-fill"></i>
    <span>عملیات با موفقیت انجام شد!</span>
</div>

<div class="alert-universal alert-error">
    <i class="bi bi-exclamation-triangle-fill"></i>
    <span>خطایی رخ داده است!</span>
</div>
```

### هدر صفحات
```html
<div class="page-header-universal">
    <div class="container">
        <h1>
            <i class="bi bi-person me-2"></i>
            عنوان صفحه
        </h1>
        <p>توضیح مختصر صفحه</p>
    </div>
</div>
```

### Breadcrumb
```html
<div class="breadcrumb-universal">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="/">خانه</a></li>
            <li class="breadcrumb-item"><a href="/profile">پروفایل</a></li>
            <li class="breadcrumb-item active">ویرایش پروفایل</li>
        </ol>
    </nav>
</div>
```

## 🎨 متغیرهای CSS

```css
:root {
    --primary-color: #667eea;
    --secondary-color: #764ba2;
    --success-color: #28a745;
    --danger-color: #dc3545;
    --warning-color: #ffc107;
    --info-color: #17a2b8;
    --border-radius: 8px;
    --border-radius-large: 12px;
    --shadow-light: 0 2px 4px rgba(0,0,0,0.1);
    --shadow-medium: 0 4px 12px rgba(0,0,0,0.15);
    --shadow-heavy: 0 8px 25px rgba(0,0,0,0.2);
    --transition-fast: all 0.2s ease;
    --transition-normal: all 0.3s ease;
    --transition-slow: all 0.5s ease;
}
```

## 🚀 انیمیشن‌ها

### کلاس‌های انیمیشن
```html
<!-- Fade in animation -->
<div class="fade-in">محتوا</div>

<!-- Slide up animation -->
<div class="slide-up">محتوا</div>

<!-- Bounce in animation -->
<div class="bounce-in">محتوا</div>

<!-- Hover effects -->
<div class="hover-lift">محتوا</div>
<div class="hover-glow">محتوا</div>
```

## 📱 Responsive Design

تمام کلاس‌ها کاملاً responsive هستند و در همه اندازه‌های صفحه نمایش به درستی کار می‌کنند:

- **Desktop**: تجربه کامل با تمام افکت‌ها
- **Tablet**: تنظیمات متناسب با صفحه
- **Mobile**: طراحی touch-friendly

## 🔧 نحوه استفاده

### 1. اضافه کردن فایل‌های CSS
```html
<head>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Inter Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Universal Styles -->
    <link href="/css/universal-styles.css" rel="stylesheet">
</head>
```

### 2. استفاده از کلاس‌ها
به جای استفاده از کلاس‌های مختلف Bootstrap، از کلاس‌های universal استفاده کنید:

**قبل:**
```html
<button class="btn btn-primary">تایید</button>
<button class="btn btn-secondary">انصراف</button>
```

**بعد:**
```html
<button class="btn-universal btn-primary">تایید</button>
<button class="btn-universal btn-secondary">انصراف</button>
```

## 📋 صفحات بروزرسانی شده

تمام صفحات زیر با استایل‌های یکسان بروزرسانی شده‌اند:

### ✅ صفحات پروفایل
- `/profile` - صفحه اصلی پروفایل
- `/profile/edit` - ویرایش پروفایل
- `/profile/ads` - آگهی‌های من
- `/profile/change-password` - تغییر رمز عبور
- `/profile/messages` - پیام‌های دریافتی

### ✅ صفحات آگهی
- `/ads/create` - ایجاد آگهی جدید
- `/ads/view/{id}` - نمایش آگهی

### ✅ صفحات احراز هویت
- `/auth/login` - ورود
- `/auth/register` - ثبت نام
- `/auth/forgot-password` - فراموشی رمز عبور
- `/auth/reset-password` - تنظیم مجدد رمز عبور

## 🎯 مزایای استایل‌های یکسان

### 👥 **تجربه کاربری بهتر**
- یکدستی در تمام صفحات
- پیش‌بینی‌پذیری رفتار elements
- navigation آسان‌تر

### 🎨 **طراحی حرفه‌ای**
- ظاهر مدرن و زیبا
- استفاده از gradient ها و سایه‌ها
- انیمیشن‌های نرم و دلنشین

### 🛠️ **نگهداری آسان**
- تغییر یکجا در تمام صفحات
- کد تمیز و سازمان‌یافته
- documentation کامل

### ⚡ **عملکرد بهتر**
- بهینه‌سازی CSS
- کاهش اندازه فایل‌ها
- loading سریع‌تر

## 📊 نمونه‌ها

برای مشاهده تمام استایل‌ها و کلاس‌ها، فایل `universal-styles-demo.html` را مشاهده کنید که شامل:

- تمام انواع دکمه‌ها
- کارت‌های مختلف
- فرم‌های نمونه
- alert های مختلف
- table ها و badge ها
- انیمیشن‌ها و افکت‌ها

## 🔄 بروزرسانی‌های آینده

- [ ] تم dark mode
- [ ] انیمیشن‌های بیشتر
- [ ] کامپوننت‌های اضافی
- [ ] بهینه‌سازی بیشتر mobile

---

**نتیجه:** حالا تمام صفحات وب‌سایت Persia Bazaar دارای طراحی یکدست، زیبا و حرفه‌ای هستند با دکمه‌ها، فرم‌ها، کارت‌ها و عناصر یکسان که تجربه کاربری بهتری ارائه می‌دهند! 🎉
