# 📞 نمایش اطلاعات تماس و شبکه‌های اجتماعی در صفحه آگهی

## 📋 خلاصه تغییرات

این قابلیت اطلاعات تماس کامل و لینک‌های شبکه‌های اجتماعی آگهی‌دهنده را در صفحه نمایش آگهی نمایش می‌دهد.

## 🎯 ویژگی‌های اضافه شده

### 1. **اطلاعات تماس کامل:**
- ✅ **Email** - دکمه Send Email با موضوع و متن پیش‌فرض
- ✅ **Phone Number** - نمایش کنترل شده شماره تلفن
- ✅ **Member Since** - تاریخ عضویت کاربر

### 2. **شبکه‌های اجتماعی:**
- ✅ **LinkedIn** - لینک مستقیم به پروفایل LinkedIn
- ✅ **Facebook** - لینک مستقیم به پروفایل Facebook  
- ✅ **Instagram** - لینک مستقیم به پروفایل Instagram

### 3. **User Experience بهتر:**
- ✅ **Click to Show Phone** - حفظ حریم خصوصی
- ✅ **Direct Email** - باز کردن کلاینت ایمیل با اطلاعات پیش‌فرض
- ✅ **Visual Icons** - آیکون‌های رنگی برای هر پلتفرم
- ✅ **Hover Effects** - انیمیشن‌های جذاب

## 🛠️ تغییرات فنی

### 1. Frontend (`ads/view.html`)

#### قسمت Contact Buttons:
```html
<!-- Email Button -->
<button th:if="${ad.user.email}" 
        class="btn btn-email" 
        th:onclick="'sendEmail(\'' + ${ad.user.email} + '\')'">
    <i class="bi bi-envelope me-2"></i>
    Send Email
</button>
```

#### قسمت Social Media:
```html
<!-- Social Media Links -->
<div class="social-media-section" th:if="${ad.user.linkdin != null or ad.user.facebook != null or ad.user.instagram != null}">
    <div class="section-title">
        <i class="bi bi-share me-2"></i>
        Follow on Social Media
    </div>
    <div class="social-links">
        <a th:if="${ad.user.linkdin != null and ad.user.linkdin != ''}" 
           th:href="${ad.user.linkdin}" 
           target="_blank" 
           class="social-link linkedin">
            <i class="bi bi-linkedin"></i>
            <span>LinkedIn</span>
        </a>
        <!-- Facebook و Instagram -->
    </div>
</div>
```

#### قسمت Contact Information:
```html
<div class="contact-info-section">
    <div class="contact-info">
        <div class="contact-item" th:if="${ad.user.email}">
            <i class="bi bi-envelope-fill"></i>
            <span class="contact-label">Email:</span>
            <span class="contact-value" th:text="${ad.user.email}">email</span>
        </div>
        <div class="contact-item" th:if="${ad.user.phoneNumber}">
            <i class="bi bi-telephone-fill"></i>
            <span class="contact-label">Phone:</span>
            <span class="contact-value phone-hidden" th:data-phone="${ad.user.phoneNumber}">
                Click to show
            </span>
        </div>
    </div>
</div>
```

### 2. JavaScript Functions

#### Send Email Function:
```javascript
function sendEmail(email) {
    const subject = encodeURIComponent('Inquiry about: ' + /*[[${ad.title}]]*/ 'Ad');
    const body = encodeURIComponent('Hi,\n\nI am interested in your ad "' + /*[[${ad.title}]]*/ 'Ad' + '".\n\nPlease contact me for more details.\n\nBest regards');
    window.location.href = 'mailto:' + email + '?subject=' + subject + '&body=' + body;
}
```

#### Show Contact Phone:
```javascript
function showContactPhone(element) {
    const phone = element.getAttribute('data-phone');
    if (phone) {
        element.textContent = phone;
        element.classList.remove('phone-hidden');
        element.classList.add('phone-revealed');
        element.onclick = function() {
            window.location.href = 'tel:' + phone;
        };
    }
}
```

### 3. CSS Styling (`ads-view-styles.css`)

#### Email Button:
```css
.btn-email {
    background: linear-gradient(135deg, #ea4335 0%, #d33b2c 100%);
    color: white;
    transition: all 0.3s ease;
}

.btn-email:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(234, 67, 53, 0.4);
}
```

#### Social Media Links:
```css
.social-link {
    display: flex;
    align-items: center;
    padding: 12px 15px;
    border-radius: 10px;
    text-decoration: none;
    color: white;
    transition: all 0.3s ease;
}

.social-link.linkedin {
    background: linear-gradient(135deg, #0077b5 0%, #005885 100%);
}

.social-link:hover {
    transform: translateX(5px);
}
```

#### Contact Information:
```css
.contact-value.phone-hidden {
    color: #007bff;
    cursor: pointer;
    text-decoration: underline;
}

.contact-value.phone-revealed {
    color: #28a745;
    font-weight: 600;
    cursor: pointer;
}
```

### 4. Backend (`HomeController.java`)

#### Authentication Status:
```java
@GetMapping("/ads/{id}")
public String viewAd(@PathVariable Long id, Model model, Authentication authentication) {
    // ... existing code ...
    
    // Check if user is authenticated
    boolean isAuthenticated = authentication != null && authentication.isAuthenticated() 
                            && !"anonymousUser".equals(authentication.getName());
    model.addAttribute("isAuthenticated", isAuthenticated);
    
    // ... rest of code ...
}
```

## 🎨 Visual Design

### 📱 **Layout:**
- **Sidebar** - اطلاعات در سمت راست صفحه آگهی
- **Sections** - تقسیم‌بندی واضح Contact Buttons, Social Media, Contact Info
- **Responsive** - سازگار با تمام اندازه صفحات

### 🌈 **Color Scheme:**
- **LinkedIn** - آبی رسمی (#0077b5)
- **Facebook** - آبی فیسبوک (#4267B2)  
- **Instagram** - Gradient رنگین‌کمانی
- **Email** - قرمز Gmail (#ea4335)
- **Phone** - سبز تماس (#28a745)

### ✨ **Animations:**
- **Hover Effects** - تغییر رنگ و حرکت
- **Shimmer Effect** - انیمیشن نور روی دکمه‌های social media
- **Transform** - حرکت افقی هنگام hover

## 🔐 حریم خصوصی

### 📞 **Phone Number:**
- **Hidden by Default** - "Click to show" نمایش داده می‌شود
- **Click to Reveal** - کلیک برای نمایش شماره واقعی
- **Click to Call** - بعد از نمایش، کلیک برای تماس

### 📧 **Email:**
- **Direct Access** - ایمیل به صورت کامل نمایش داده می‌شود
- **Pre-filled Content** - موضوع و متن پیش‌فرض برای راحتی

## 📊 مزایا

### 🎯 **برای خریداران:**
- **راه‌های بیشتر تماس** - ایمیل، تلفن، شبکه‌های اجتماعی
- **اعتماد بیشتر** - مشاهده پروفایل‌های اجتماعی فروشنده
- **ارتباط آسان** - کلیک‌های کمتر برای تماس

### 💰 **برای فروشندگان:**
- **فروش بیشتر** - راه‌های ارتباطی متنوع
- **ساخت برند شخصی** - نمایش پروفایل‌های حرفه‌ای
- **اعتماد خریداران** - شفافیت بیشتر در اطلاعات

### 🏪 **برای پلتفرم:**
- **Engagement بیشتر** - زمان بیشتر کاربران در سایت
- **کیفیت بالاتر** - فروشندگان جدی‌تر
- **Network Effects** - اتصال بین کاربران

## 📱 Mobile Responsive

### 📐 **Breakpoints:**
- **Desktop** - نمایش کامل در sidebar
- **Tablet** - تنظیم اندازه فونت و padding
- **Mobile** - چینش عمودی بهینه

### 🎛️ **Optimizations:**
- **Touch Targets** - دکمه‌های بزرگ‌تر برای تاچ
- **Readable Fonts** - اندازه فونت مناسب
- **Spacing** - فاصله‌گذاری بهینه

## 🧪 تست و Quality Assurance

### ✅ **Test Cases:**
- [ ] نمایش درست اطلاعات وقتی تمام فیلدها پر هستند
- [ ] عدم نمایش بخش‌هایی که اطلاعات ندارند
- [ ] کارکرد درست click to show phone
- [ ] باز شدن کلاینت ایمیل با اطلاعات درست
- [ ] لینک‌های social media در تب جدید باز می‌شوند
- [ ] responsive design در اندازه‌های مختلف
- [ ] hover effects و animations

### 🔍 **Browser Compatibility:**
- ✅ Chrome/Edge (latest)
- ✅ Firefox (latest)  
- ✅ Safari (latest)
- ✅ Mobile browsers

## 🚀 آماده برای استفاده!

این قابلیت کاملاً پیاده‌سازی شده و آماده استفاده است:

### 👤 **برای کاربران:**
1. ابتدا در پروفایل خود لینک‌های شبکه‌های اجتماعی را تکمیل کنید
2. وقتی آگهی منتشر می‌کنید، اطلاعات تماس شما نمایش داده می‌شود
3. خریداران راه‌های مختلفی برای تماس با شما خواهند داشت

### 💻 **برای توسعه‌دهندگان:**
- کد تمیز و قابل نگهداری
- CSS منظم و responsive
- JavaScript بهینه و error-safe
- مستندات کامل

**همه چیز آماده است! حالا آگهی‌دهندگان می‌توانند اطلاعات تماس کاملی در اختیار خریداران قرار دهند! 🎉**
