# 🏪 Persia Bazaar - بازار ایران

> **پلتفرم آگهی آنلاین پیشرفته با Spring Boot و معماری مدرن**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-yellow.svg)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/API-Swagger%20Documented-green.svg)](https://swagger.io/)

## 📋 توضیحات

**Persia Bazaar** یک سیستم مدیریت آگهی‌های آنلاین پیشرفته است که با معماری مدرن و امنیت بالا طراحی شده است. این پلتفرم امکانات کاملی برای ایجاد، مدیریت و جستجوی آگهی‌ها با سیستم احراز هویت پیشرفته و مدیریت مجوزها فراهم می‌کند.

## ✨ ویژگی‌های پیاده‌سازی شده

### 🔐 **Authentication & Security - کامل**
- ✅ JWT-based authentication با 10 ساعت انقضا
- ✅ Role-based authorization (RBAC)
- ✅ Method-level security با @PreAuthorize
- ✅ Custom permission evaluator
- ✅ BCrypt password encoding (strength 12)
- ✅ User status management (6 حالت مختلف)

### � **API Documentation - کامل**
- ✅ Swagger/OpenAPI 3.0 integration
- ✅ Security scheme documentation
- ✅ Complete endpoint documentation
- ✅ DTO schema annotations
- ✅ Interactive Swagger UI

### ⚡ **Exception Handling & Validation - کامل**
- ✅ Global exception handler (15+ exception types)
- ✅ Custom exception classes
- ✅ Bean validation with Jakarta annotations
- ✅ Custom validation (@ValidPrice)
- ✅ Standardized error responses

## 🛠️ تکنولوژی‌های استفاده شده

| دسته‌بندی | تکنولوژی | نسخه |
|----------|----------|------|
| **Backend Framework** | Spring Boot | 3.1.5 |
| **Language** | Java | 17 |
| **Database** | PostgreSQL | 15+ |
| **Security** | Spring Security + JWT | - |
| **Documentation** | Swagger/OpenAPI | 3.0 |
| **Validation** | Jakarta Bean Validation | 3.0 |
| **Build Tool** | Maven | 3.6+ |
| **ORM** | JPA/Hibernate | - |

## 🚀 راه‌اندازی سریع

### پیش‌نیازها
- ☑️ Java 17+
- ☑️ PostgreSQL 15+
- ☑️ Maven 3.6+
- ☑️ Git

### نصب و اجرا (5 دقیقه)

#### **1. دریافت پروژه:**
```bash
git clone https://gitlab.com/webrayan/iran-bazaar.git
cd iran-bazaar
```

#### **2. تنظیم دیتابیس:**
```sql
-- ایجاد دیتابیس
CREATE DATABASE bazaar;
```

#### **3. تنظیم application.properties:**
```properties
# کپی و ویرایش تنظیمات
cp src/main/resources/application.properties.example src/main/resources/application.properties

# تنظیم اتصال به دیتابیس
spring.datasource.url=jdbc:postgresql://localhost:5432/bazaar
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

#### **4. اجرای پروژه:**
```bash
# نصب dependencies و اجرا
mvn clean install
mvn spring-boot:run
```

#### **5. تست راه‌اندازی:**
- **🌐 Application**: http://localhost:8005
- **📚 Swagger UI**: http://localhost:8005/swagger-ui.html
- **📋 API Docs**: http://localhost:8005/v3/api-docs

#### **6. اولین Login:**
```bash
# تست API با admin user
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123"}'
```

**🎉 تبریک! پروژه شما آماده است.**

## 📚 مستندات جامع

| نوع مستند | توضیح | لینک |
|----------|--------|------|
| **🚀 Quick Start** | راه‌اندازی 5 دقیقه‌ای | [docs/01-getting-started/QUICK_START.md](docs/01-getting-started/QUICK_START.md) |
| **📖 Implementation Guide** | راهنمای کامل پیاده‌سازی | [docs/02-implementation/IMPLEMENTATION_GUIDE.md](docs/02-implementation/IMPLEMENTATION_GUIDE.md) |
| **📡 API Usage Examples** | نمونه‌های عملی استفاده | [docs/03-api-reference/API_USAGE_EXAMPLES.md](docs/03-api-reference/API_USAGE_EXAMPLES.md) |
| **🔐 Security Guide** | راهنمای کامل امنیت | [docs/04-security/SECURITY.md](docs/04-security/SECURITY.md) |
| **⚡ Exception Handling** | مدیریت خطا و validation | [docs/02-implementation/EXCEPTION_HANDLING.md](docs/02-implementation/EXCEPTION_HANDLING.md) |
| **📋 API Documentation** | مستندات Swagger کامل | [docs/03-api-reference/API_DOCUMENTATION.md](docs/03-api-reference/API_DOCUMENTATION.md) |
| **📚 Documentation Index** | فهرست کامل مستندات | [docs/DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md) |

## 🔗 دسترسی سریع

### **Development URLs:**
- 🏠 **Application**: http://localhost:8005
- 📚 **Swagger UI**: http://localhost:8005/swagger-ui.html
- 📊 **API Documentation**: http://localhost:8005/v3/api-docs
- 🔍 **Health Check**: http://localhost:8005/actuator/health

### **Default Credentials:**
```
Username: admin
Password: 123
Role: SYSTEM_ADMIN
```

## 🧪 تست سریع

### **1. دریافت Token:**
```bash
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123"}'
```

### **2. استفاده از API:**
```bash
# تنظیم token
export TOKEN="YOUR_JWT_TOKEN"

# دریافت لیست کاربران
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8005/api/acl/users
```

## 📁 ساختار پروژه

```
iran-bazaar/
├── docs/                          # 📚 مستندات کامل
│   ├── QUICK_START.md
│   ├── IMPLEMENTATION_GUIDE.md
│   ├── API_USAGE_EXAMPLES.md
│   └── ...
├── src/main/java/com/webrayan/bazaar/
│   ├── core/                      # 🔧 هسته سیستم
│   │   ├── config/               # تنظیمات (Security, OpenAPI)
│   │   ├── exception/            # مدیریت خطا
│   │   └── validation/           # اعتبارسنجی سفارشی
│   └── modules/                   # 📦 ماژول‌های کسب‌وکار
│       ├── acl/                  # احراز هویت و مجوزها
│       ├── ad/                   # مدیریت آگهی‌ها
│       └── ...
└── target/                        # 🏗️ خروجی build
```

## 🔧 Commands مفید

```bash
# اجرای تست‌ها
mvn test

# Build برای production
mvn clean package

# اجرا در حالت development
mvn spring-boot:run -Dspring.profiles.active=dev

# مشاهده logs
tail -f logs/spring.log

# بررسی health
curl http://localhost:8005/actuator/health
```

## 🛠️ مراحل توسعه بعدی

### **🎯 Priority 1: Data Transfer Layer**
- [ ] DTO mapping با MapStruct
- [ ] Response pagination
- [ ] Advanced filtering

### **🎯 Priority 2: File Upload System**
- [ ] Image upload برای آگهی‌ها
- [ ] File validation
- [ ] Storage management

### **🎯 Priority 3: Caching Strategy**
- [ ] Redis integration
- [ ] Cache annotations
- [ ] Cache invalidation

## 🆘 پشتیبانی

**مشکل دارید؟**
1. 📋 بررسی `logs/spring.log`
2. 📖 مراجعه به `docs/IMPLEMENTATION_GUIDE.md`
3. 🧪 بررسی `target/surefire-reports/` برای خطاهای test
4. 🌐 تست از طریق Swagger UI

## 🤝 مشارکت

1. Fork پروژه
2. ایجاد branch جدید (`git checkout -b feature/amazing-feature`)
3. Commit تغییرات (`git commit -m 'Add amazing feature'`)
4. Push به branch (`git push origin feature/amazing-feature`)
5. ایجاد Pull Request

## 📄 لایسنس

این پروژه تحت لایسنس MIT منتشر شده است - فایل [LICENSE](LICENSE) را برای جزئیات مشاهده کنید.

## 👥 تیم توسعه

- **Lead Developer**: [Webrayan Team](mailto:info@webrayan.com)
- **Project Manager**: Persia Bazaar Team

---

**🎉 Persia Bazaar - آماده برای توسعه و استفاده!**

### سایر مستندات
- **فهرست کامل مستندات**: [docs/README.md](docs/README.md)

## 🚀 استفاده سریع

### احراز هویت
```bash
# ورود به سیستم
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123"}'
```

### حساب‌های پیش‌فرض
- **ادمین**: `admin` / `123`
- **کاربر عادی**: `user` / `123`

## 🏗️ ساختار پروژه

```
src/main/java/com/webrayan/bazaar/
├── core/                 # هسته اصلی سیستم
│   ├── config/          # تنظیمات (Security, OpenAPI)
│   ├── security/        # امنیت و احراز هویت
│   └── common/          # کلاس‌های مشترک
├── modules/             # ماژول‌های کاربردی
│   ├── acl/            # مدیریت کاربران و دسترسی‌ها
│   ├── ads/            # مدیریت آگهی‌ها
│   ├── catalog/        # مدیریت محصولات
│   └── sale/           # سیستم فروش
└── config/             # تنظیمات اولیه
```

## 🔧 Development

### تست API ها
پس از راه‌اندازی، می‌توانید API ها را از طریق Swagger UI تست کنید:
1. به آدرس http://localhost:8005/swagger-ui.html بروید
2. از endpoint `/api/auth/login` برای دریافت token استفاده کنید
3. Token را در قسمت "Authorize" وارد کنید
4. API های مختلف را تست کنید

### اضافه کردن Controller جدید
```java
@Tag(name = "Controller Name", description = "توضیحات")
@RestController
@RequestMapping("/api/path")
public class YourController {
    
    @Operation(summary = "خلاصه عملیات")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Entity>>> getAll() {
        // کد شما
    }
}
```

## 🛡️ امنیت

- تمام API ها با JWT محافظت می‌شوند
- سیستم نقش‌بندی پیشرفته (RBAC)
- تأیید صلاحیت در سطح method
- رمزگذاری رمزهای عبور با BCrypt

## 📝 API Endpoints

### Authentication
- `POST /api/auth/login` - ورود کاربر
- `POST /api/auth/signup` - ثبت نام کاربر

### User Management
- `GET /api/acl/users` - لیست کاربران
- `POST /api/acl/users` - ایجاد کاربر
- `PUT /api/acl/users/{id}` - ویرایش کاربر

### Ads Management
- `GET /api/ads` - لیست آگهی‌ها
- `POST /api/ads` - ایجاد آگهی
- `PUT /api/ads/{id}` - ویرایش آگهی

## 🤝 مشارکت

1. Fork کنید
2. Feature branch ایجاد کنید (`git checkout -b feature/AmazingFeature`)
3. تغییرات را commit کنید (`git commit -m 'Add some AmazingFeature'`)
4. Push کنید (`git push origin feature/AmazingFeature`)
5. Pull Request ایجاد کنید

## 📞 تماس

- **تیم توسعه**: WebRayan Team
- **ایمیل**: info@webrayan.com
- **وبسایت**: https://webrayan.com

## 📄 مجوز

این پروژه تحت مجوز MIT منتشر شده است. برای جزئیات بیشتر فایل [LICENSE](LICENSE) را مطالعه کنید.

---

**Persia Bazaar** - پلتفرم تجارت الکترونیک نسل جدید ایران 🇮🇷
