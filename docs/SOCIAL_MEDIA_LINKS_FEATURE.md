# 📱 قابلیت شبکه‌های اجتماعی در پروفایل کاربران

## 📋 خلاصه تغییرات

این قابلیت امکان اضافه کردن و ویرایش لینک‌های شبکه‌های اجتماعی (LinkedIn, Facebook, Instagram) را به پروفایل کاربران اضافه می‌کند.

## 🗄️ تغییرات Database

### فیلدهای موجود در جدول `acl_users`:
```sql
-- این فیلدها قبلاً در User entity موجود بودند:
linkdin VARCHAR(100)
facebook VARCHAR(100) 
instagram VARCHAR(100)
```

## 🛠️ تغییرات Backend

### 1. UserService.java
```java
// متد جدید اضافه شده:
public User updateSocialMediaLinks(Long userId, String linkdin, String facebook, String instagram)
```

**ویژگی‌ها:**
- ✅ Clean کردن URL ها
- ✅ اضافه کردن `https://` به URL های بدون protocol
- ✅ مدیریت null و empty values
- ✅ Logging عملیات

### 2. ProfileController.java
```java
// متد update بروزرسانی شده:
@PostMapping("/update")
public String updateProfile(
    @RequestParam String firstName,
    @RequestParam String lastName,
    @RequestParam String email,
    @RequestParam String phoneNumber,
    @RequestParam(required = false) String linkdin,
    @RequestParam(required = false) String facebook,
    @RequestParam(required = false) String instagram,
    RedirectAttributes redirectAttributes)
```

## 🎨 تغییرات Frontend

### 1. صفحه ویرایش پروفایل (`profile/edit.html`)

#### قسمت جدید اضافه شده:
```html
<!-- Social Media Section -->
<div class="form-section">
    <h5 class="section-title">
        <i class="bi bi-share"></i>
        Social Media Links
    </h5>
    
    <!-- LinkedIn -->
    <div class="input-group">
        <span class="input-group-text bg-primary text-white">
            <i class="bi bi-linkedin"></i>
        </span>
        <input type="url" class="form-control" id="linkdin" name="linkdin" 
               th:value="${user.linkdin}" 
               placeholder="https://linkedin.com/in/your-profile">
    </div>
    
    <!-- Facebook -->
    <div class="input-group">
        <span class="input-group-text text-white" style="background-color: #4267B2;">
            <i class="bi bi-facebook"></i>
        </span>
        <input type="url" class="form-control" id="facebook" name="facebook" 
               th:value="${user.facebook}" 
               placeholder="https://facebook.com/your-profile">
    </div>
    
    <!-- Instagram -->
    <div class="input-group">
        <span class="input-group-text text-white" style="background: linear-gradient(45deg, #f09433 0%,#e6683c 25%,#dc2743 50%,#cc2366 75%,#bc1888 100%);">
            <i class="bi bi-instagram"></i>
        </span>
        <input type="url" class="form-control" id="instagram" name="instagram" 
               th:value="${user.instagram}" 
               placeholder="https://instagram.com/your-profile">
    </div>
</div>
```

#### JavaScript Validation:
- ✅ **Real-time validation** برای URL ها
- ✅ **Platform-specific validation** (LinkedIn, Facebook, Instagram)
- ✅ **Empty values** مجاز هستند
- ✅ **Error messages** به زبان فارسی

### 2. نمایش در Profile (`profile/profile.html`)

```html
<!-- Social Media Links -->
<div class="social-links" th:if="${user.linkdin != null or user.facebook != null or user.instagram != null}">
    <div class="social-links-title">
        <small class="text-muted">Follow me:</small>
    </div>
    <div class="social-icons">
        <a th:if="${user.linkdin != null and user.linkdin != ''}" 
           th:href="${user.linkdin}" 
           target="_blank" 
           class="social-icon linkedin"
           title="LinkedIn">
            <i class="bi bi-linkedin"></i>
        </a>
        <!-- Facebook و Instagram به همین شکل -->
    </div>
</div>
```

### 3. CSS Styling (`profile-styles.css`)

```css
/* Social Media Icons */
.social-links {
    margin-top: 20px;
    padding: 15px;
    background: rgba(255,255,255,0.1);
    border-radius: 15px;
    backdrop-filter: blur(10px);
}

.social-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    color: white;
    transition: all 0.3s ease;
}

.social-icon:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.3);
}
```

## 🧪 تست‌ها

### 1. Unit Tests (`UserSocialMediaTest.java`)
- ✅ تست به‌روزرسانی لینک‌های شبکه‌های اجتماعی
- ✅ تست clean کردن URL ها
- ✅ تست مدیریت null و empty values

### 2. Integration Tests (`ProfileEditControllerTest.java`)
- ✅ تست بارگذاری صفحه ویرایش
- ✅ تست به‌روزرسانی با لینک‌های social media
- ✅ تست به‌روزرسانی با فیلدهای خالی

## 🔧 نحوه استفاده

### 1. ویرایش پروفایل:
1. به `/profile/edit` بروید
2. در قسمت "Social Media Links" لینک‌هایتان را وارد کنید
3. روی "Save Changes" کلیک کنید

### 2. نمایش در پروفایل:
- لینک‌های وارد شده در sidebar پروفایل نمایش داده می‌شوند
- فقط لینک‌هایی که پر شده‌اند نمایش داده می‌شوند

## ✨ ویژگی‌ها

### 🎯 **User Experience:**
- **Bootstrap Icons** برای آیکون‌های زیبا
- **Colorful input groups** مطابق با رنگ هر پلتفرم
- **Hover effects** روی آیکون‌های social media
- **Real-time validation** با پیام‌های خطای فارسی

### 🔒 **امنیت:**
- **URL validation** قبل از ذخیره
- **XSS protection** با escape کردن output
- **Required=false** برای اختیاری بودن فیلدها

### 📱 **Responsive:**
- **Mobile-friendly** design
- **Bootstrap 5** compatibility
- **Modern CSS** با backdrop-filter

## 🚀 مزایا

1. **افزایش اعتماد خریداران** با نمایش پروفایل‌های اجتماعی
2. **Professional look** برای فروشندگان
3. **Easy networking** بین کاربران
4. **SEO benefits** با لینک‌های خارجی

## 📝 نکات مهم

### ✅ **Do's:**
- همیشه URL کامل وارد کنید (با https://)
- لینک‌های معتبر و فعال استفاده کنید
- پروفایل‌های عمومی را لینک کنید

### ❌ **Don'ts:**
- لینک‌های spam یا مضر استفاده نکنید
- پروفایل‌های خصوصی لینک نکنید
- لینک‌های شکسته نگذارید

## 🎉 **آماده برای استفاده!**

این قابلیت کاملاً پیاده‌سازی شده و آماده استفاده است. کاربران می‌توانند:
- لینک‌های شبکه‌های اجتماعی خود را اضافه کنند
- آن‌ها را در پروفایل نمایش دهند  
- آسان آن‌ها را ویرایش کنند

**تست کنید و لذت ببرید!** 🚀
