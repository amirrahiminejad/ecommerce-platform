# 📋 راهنمای استفاده از کامپوننت Confirm Modal

## 🚀 نحوه اضافه کردن به صفحه

### 1. اضافه کردن Modal به Template:
```html
<!-- اضافه کردن در انتهای body -->
<div th:replace="~{components/confirm-modal :: confirm-modal}"></div>

<!-- اضافه کردن JavaScript در انتهای صفحه -->
<script th:replace="~{components/confirm-modal :: confirm-modal-js}"></script>
```

## 💡 روش‌های استفاده

### 1. استفاده ساده:
```javascript
// تایید ساده
showConfirm('آیا از این عملیات اطمینان دارید؟', function() {
    // کد اجرایی پس از تایید
    console.log('کاربر تایید کرد');
});
```

### 2. تایید حذف:
```javascript
// حذف آگهی
confirmDelete('آگهی پراید 131', function() {
    deleteAd(123);
});

// حذف کاربر
confirmDelete('کاربر احمد احمدی', function() {
    deleteUser(456);
});
```

### 3. تایید خروج:
```javascript
confirmLogout(function() {
    window.location.href = '/auth/logout';
});
```

### 4. استفاده پیشرفته:
```javascript
confirmModal.show({
    title: 'تایید انتشار آگهی',
    message: 'آیا می‌خواهید این آگهی را منتشر کنید؟',
    description: 'پس از انتشار، آگهی برای همه قابل مشاهده خواهد بود.',
    confirmText: 'انتشار',
    confirmClass: 'btn-success',
    onConfirm: function() {
        publishAd(adId);
    },
    onCancel: function() {
        console.log('کاربر منصرف شد');
    }
});
```

## ⚙️ تنظیمات قابل استفاده

| پارامتر | نوع | پیش‌فرض | توضیح |
|---------|-----|----------|-------|
| `title` | string | 'تایید عملیات' | عنوان modal |
| `message` | string | 'آیا از انجام این عملیات اطمینان دارید؟' | پیام اصلی |
| `description` | string | 'این عملیات قابل بازگشت نیست.' | توضیحات تکمیلی |
| `confirmText` | string | 'تایید' | متن دکمه تایید |
| `confirmClass` | string | 'btn-danger' | کلاس دکمه تایید |
| `onConfirm` | function | null | تابع اجرا پس از تایید |
| `onCancel` | function | null | تابع اجرا پس از انصراف |

## 🎨 کلاس‌های دکمه موجود

```javascript
'btn-primary'   // آبی - برای عملیات عادی
'btn-success'   // سبز - برای تایید/موفقیت
'btn-warning'   // زرد - برای اخطار
'btn-danger'    // قرمز - برای حذف/خطر
'btn-info'      // آبی روشن - برای اطلاعات
'btn-secondary' // خاکستری - برای عملیات ثانویه
```

## 📝 مثال‌های کاربردی

### مثال 1: حذف آگهی در صفحه پروفایل
```html
<button onclick="confirmDeleteAd(${ad.id}, '${ad.title}')" 
        class="btn btn-outline-danger btn-sm">
    <i class="bi bi-trash"></i> حذف
</button>

<script>
function confirmDeleteAd(adId, adTitle) {
    confirmDelete(adTitle, function() {
        fetch(`/api/ads/${adId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            }
        });
    });
}
</script>
```

### مثال 2: خروج از سیستم
```html
<a href="#" onclick="handleLogout(event)" class="dropdown-item">
    <i class="bi bi-box-arrow-right"></i> خروج
</a>

<script>
function handleLogout(event) {
    event.preventDefault();
    confirmLogout(function() {
        window.location.href = '/auth/logout';
    });
}
</script>
```

### مثال 3: تایید ارسال فرم
```html
<form id="contactForm" onsubmit="return false;">
    <!-- فیلدهای فرم -->
    <button type="button" onclick="confirmSubmitForm()" class="btn btn-primary">
        ارسال پیام
    </button>
</form>

<script>
function confirmSubmitForm() {
    const form = document.getElementById('contactForm');
    
    showConfirm(
        'آیا از ارسال این پیام اطمینان دارید؟',
        function() {
            form.submit();
        },
        {
            title: 'تایید ارسال',
            confirmText: 'ارسال',
            confirmClass: 'btn-primary'
        }
    );
}
</script>
```

## 🔧 نکات مهم

1. **Bootstrap 5 ضروری است**: این کامپوننت نیاز به Bootstrap 5 دارد
2. **jQuery اختیاری**: فقط از vanilla JavaScript استفاده می‌کند
3. **RTL Support**: کاملاً از راست به چپ پشتیبانی می‌کند
4. **Responsive**: روی همه سایزهای صفحه کار می‌کند
5. **Keyboard Support**: با کلیدهای ESC و Enter کار می‌کند

## 🚨 خطاهای متداول

### خطا: ConfirmModal is not initialized
```javascript
// ❌ اشتباه - فراخوانی قبل از آماده شدن DOM
showConfirm('پیام', callback);

// ✅ درست - فراخوانی پس از آماده شدن
document.addEventListener('DOMContentLoaded', function() {
    showConfirm('پیام', callback);
});
```

### خطا: Bootstrap modal not working
```html
<!-- ✅ مطمئن شوید Bootstrap CSS و JS لود شده -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
```
