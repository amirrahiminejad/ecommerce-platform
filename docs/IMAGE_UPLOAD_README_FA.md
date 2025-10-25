# کامپوننت آپلود و مدیریت تصاویر

یک کامپوننت جامع، ریسپانسیو و پر از امکانات برای مدیریت آپلود تصاویر با قابلیت کشیدن و رها کردن، پیش‌نمایش و مدیریت تصاویر.

## ویژگی‌ها

- ✨ **کشیدن و رها کردن** - رابط کاربری بصری برای کشیدن و رها کردن فایل‌ها
- 🖼️ **پیش‌نمایش تصاویر** - پیش‌نمایش زنده تصاویر با نمای مودال
- 📱 **طراحی ریسپانسیو** - کار مناسب روی موبایل و دسکتاپ
- 🎨 **تم‌های متعدد** - تم‌های پیش‌فرض، مدرن، مینیمال، تاریک و رنگارنگ
- 🔄 **قابلیت مرتب‌سازی** - کشیدن برای تغییر ترتیب تصاویر
- 📊 **پیگیری پیشرفت** - نمایش پیشرفت آپلود
- ⚡ **اعتبارسنجی فایل** - بررسی اندازه و نوع فایل
- 🚫 **مدیریت خطا** - پیام‌های خطای کامل
- 🎯 **قابل شخصی‌سازی** - گزینه‌های پیکربندی گسترده
- 🔌 **یکپارچه‌سازی آسان** - API جاوااسکریپت ساده

## شروع سریع

### 1. اضافه کردن وابستگی‌ها

```html
<!-- Bootstrap 5 (ضروری) -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

<!-- فونت Inter (پیشنهادی) -->
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

<!-- فایل‌های کامپوننت -->
<script src="/js/image-upload-component.js"></script>
<link href="/css/image-upload-component.css" rel="stylesheet"> <!-- اختیاری: برای تم‌های اضافی -->
```

### 2. ایجاد کانتینر HTML

```html
<div id="myImageUploader"></div>
```

### 3. راه‌اندازی کامپوننت

```javascript
const imageUploader = new ImageUploadManager('myImageUploader', {
    maxFiles: 10,
    maxFileSize: 5 * 1024 * 1024, // 5MB
    theme: 'modern',
    uploadUrl: '/api/images/upload',
    deleteUrl: '/api/images/delete'
});
```

## گزینه‌های پیکربندی

```javascript
const options = {
    // محدودیت‌های فایل
    maxFiles: 10,                    // حداکثر تعداد فایل
    maxFileSize: 5 * 1024 * 1024,   // حداکثر اندازه فایل به بایت (5MB)
    acceptedTypes: [                 // نوع فایل‌های قابل قبول
        'image/jpeg', 'image/jpg', 
        'image/png', 'image/gif', 
        'image/webp'
    ],
    
    // نقاط پایانی API
    uploadUrl: '/api/images/upload', // نقطه پایانی آپلود
    deleteUrl: '/api/images/delete', // نقطه پایانی حذف
    
    // گزینه‌های رابط کاربری
    theme: 'default',               // تم: 'default', 'modern', 'minimal', 'dark', 'colorful'
    showPreview: true,              // نمایش مودال پیش‌نمایش
    allowReorder: true,             // اجازه کشیدن برای تغییر ترتیب
    allowDelete: true,              // نمایش دکمه‌های حذف
};
```

## تم‌ها

### تم‌های در دسترس
- **default** - تم کلاسیک با گرادیان آبی
- **modern** - تم معاصر بنفش  
- **minimal** - تم تمیز سیاه و سفید
- **dark** - تم حالت تاریک
- **colorful** - تم روشن و پر رنگ

### چیدمان‌های سفارشی
کلاس‌های CSS را به کانتینر اضافه کنید برای چیدمان‌های مختلف:
- `compact` - چیدمان کوچک‌تر
- `large` - چیدمان بزرگ‌تر  
- `rounded` - گوشه‌های گرد
- `list-view` - لیست به جای شبکه
- `grid-2`, `grid-3`, `grid-4`, `grid-5` - ستون‌های شبکه ثابت
- `animated` - انیمیشن‌های بهبود یافته

```html
<div id="uploader" class="compact rounded animated"></div>
```

## متدهای API

```javascript
// دریافت همه تصاویر (شامل موارد در حال آپلود)
const allImages = imageUploader.getImages();

// دریافت فقط تصاویر آپلود شده با موفقیت
const uploadedImages = imageUploader.getUploadedImages();

// پاک کردن همه تصاویر
imageUploader.clearAll();

// تنظیم تصاویر به صورت برنامه‌نویسی
imageUploader.setImages([
    {
        id: 'img1',
        name: 'photo.jpg',
        size: 245760,
        status: 'success',
        preview: '/path/to/preview.jpg',
        url: '/path/to/image.jpg',
        serverId: 'server_id_123'
    }
]);

// پیش‌نمایش تصویر خاص
imageUploader.previewImage('imageId');
```

## نمونه‌های یکپارچه‌سازی

### یکپارچه‌سازی فرم ساده

```html
<form action="/ads/create" method="post">
    <input type="text" name="title" placeholder="عنوان">
    
    <!-- آپلودر تصاویر -->
    <div id="imageUploader"></div>
    <div id="imageInputs"></div> <!-- ورودی‌های مخفی اینجا اضافه می‌شوند -->
    
    <button type="submit">ارسال</button>
</form>

<script>
const uploader = new ImageUploadManager('imageUploader');

// اضافه کردن ورودی‌های مخفی هنگام ارسال فرم
document.querySelector('form').addEventListener('submit', function() {
    const images = uploader.getUploadedImages();
    const container = document.getElementById('imageInputs');
    
    container.innerHTML = '';
    images.forEach((img, index) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = `imageUrls[${index}]`;
        input.value = img.url;
        container.appendChild(input);
    });
});
</script>
```

### راه‌اندازی خودکار با ویژگی‌های داده

```html
<div id="autoUploader" 
     data-image-upload='{
        "maxFiles": 5,
        "theme": "modern",
        "uploadUrl": "/api/upload"
     }'></div>
```

### یکپارچه‌سازی AJAX

```javascript
// ارسال داده‌های فرم همراه با تصاویر از طریق AJAX
async function submitWithImages() {
    const images = uploader.getUploadedImages();
    const formData = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        imageUrls: images.map(img => img.url),
        imageIds: images.map(img => img.serverId)
    };
    
    const response = await fetch('/api/ads/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    });
    
    return response.json();
}
```

## یکپارچه‌سازی بک‌اند

### نمونه نقطه پایانی آپلود (Spring Boot)

```java
@PostMapping("/api/images/upload")
public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                   @RequestParam("id") String id) {
    try {
        // ذخیره فایل
        String filename = fileService.saveImage(file);
        String url = "/uploads/images/" + filename;
        
        // بازگشت پاسخ
        Map<String, Object> response = new HashMap<>();
        response.put("id", UUID.randomUUID().toString());
        response.put("url", url);
        response.put("filename", filename);
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }
}
```

### نمونه نقطه پایانی حذف

```java
@DeleteMapping("/api/images/delete/{id}")
public ResponseEntity<?> deleteImage(@PathVariable String id) {
    try {
        fileService.deleteImage(id);
        return ResponseEntity.ok(Map.of("success", true));
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }
}
```

## رویدادها و شخصی‌سازی

### مدیریت رویداد سفارشی

```javascript
const uploader = new ImageUploadManager('uploader');

// بازنویسی متد آپلود برای مدیریت سفارشی
const originalUpload = uploader.uploadImage;
uploader.uploadImage = async function(imageData) {
    console.log('شروع آپلود:', imageData.name);
    
    // نمایش لودر سفارشی
    showCustomLoader(true);
    
    try {
        await originalUpload.call(this, imageData);
        console.log('آپلود موفق:', imageData.name);
    } catch (error) {
        console.error('آپلود ناموفق:', error);
        showCustomError(error.message);
    } finally {
        showCustomLoader(false);
    }
};
```

### اعتبارسنجی سفارشی

```javascript
const uploader = new ImageUploadManager('uploader');

// بازنویسی اعتبارسنجی
uploader.validateFile = function(file) {
    // منطق اعتبارسنجی سفارشی
    if (file.name.includes('forbidden')) {
        this.showError('نام فایل شامل کلمات ممنوع است');
        return false;
    }
    
    // فراخوانی اعتبارسنجی اصلی
    return ImageUploadManager.prototype.validateFile.call(this, file);
};
```

## شخصی‌سازی استایل

### خصوصیات سفارشی CSS

```css
.image-upload-manager {
    --primary-color: #your-color;
    --secondary-color: #your-color;
    --success-color: #your-color;
    --danger-color: #your-color;
    --border-color: #your-color;
    --bg-light: #your-color;
}
```

### تم سفارشی

```css
.image-upload-manager[data-theme="custom"] {
    --primary-color: #ff6b6b;
    --secondary-color: #4ecdc4;
    /* ... سایر خصوصیات */
}
```

## پشتیبانی مرورگر

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+
- مرورگرهای موبایل (iOS Safari, Chrome Mobile)

## وابستگی‌ها

- Bootstrap 5.3+ (فریم‌ورک CSS)
- Bootstrap Icons (فونت آیکون)
- مرورگر مدرن با پشتیبانی ES6+

## ساختار فایل

```
/static/
├── js/
│   └── image-upload-component.js     # کامپوننت اصلی
├── css/
│   └── image-upload-component.css    # استایل‌های اضافی اختیاری
├── image-upload-demo.html            # صفحه نمایش کامل
└── image-upload-integration-examples.html  # نمونه‌های یکپارچه‌سازی
```

## مجوز

این کامپوننت برای استفاده در پروژه‌های شما رایگان است.

## پشتیبانی

برای مسائل و سوالات، لطفاً ابتدا صفحه نمایش و نمونه‌های یکپارچه‌سازی را بررسی کنید. کامپوننت طوری طراحی شده که بدون هیچ تغییری در بک‌اند کار کند - کاملاً متمرکز بر فرانت‌اند است.

## راهنمای استفاده سریع

### نصب و راه‌اندازی:

1. فایل‌های CSS و JS را در قالب خود قرار دهید
2. عنصر div برای کانتینر بسازید
3. با `new ImageUploadManager()` راه‌اندازی کنید
4. تمام! 🎉

### مثال کامل:

```html
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <title>آپلود تصاویر</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h2>آپلود تصاویر آگهی</h2>
        <div id="adImageUploader"></div>
    </div>
    
    <script src="/js/image-upload-component.js"></script>
    <script>
        const uploader = new ImageUploadManager('adImageUploader', {
            maxFiles: 8,
            theme: 'modern',
            uploadUrl: '/api/ads/images/upload'
        });
    </script>
</body>
</html>
```

### ویژگی‌های کلیدی:

- 🚀 **سریع و سبک** - بدون وابستگی اضافی
- 🎨 **زیبا** - طراحی مدرن و حرفه‌ای
- 📱 **موبایل دوست** - کاملاً ریسپانسیو
- 🔧 **قابل انطباق** - سازگار با هر بک‌اند
- 🌐 **چندزبانه** - پشتیبانی از فارسی و انگلیسی

این کامپوننت کاملاً آماده استفاده است و نیاز به هیچ تغییری در کد بک‌اند یا صفحات موجود ندارد.
