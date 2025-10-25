# راهنمای سریع Swagger - پروژه Persia Bazaar

## دسترسی سریع

### URL های مهم:
- **Swagger UI**: `http://localhost:8005/swagger-ui.html`
- **API Docs JSON**: `http://localhost:8005/v3/api-docs`

## احراز هویت سریع

### 1. دریافت Token:
```bash
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123"
  }'
```

### 2. استفاده از Token:
در Swagger UI روی دکمه "Authorize" کلیک کنید و token را با پیشوند `Bearer ` وارد کنید:
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Controller های مستندسازی شده

### ✅ تکمیل شده:
- **AuthController** (`/api/auth`) - احراز هویت
- **UserController** (`/api/acl/users`) - مدیریت کاربران  
- **AdController** (`/api/ads`) - مدیریت آگهی‌ها

### ⏳ در حال تکمیل:
- CountryController
- SettingController
- CategoryController
- ProductController
- OrderController
- CartController

## مثال‌های سریع API

### ورود به سیستم:
```json
POST /api/auth/login
{
  "username": "admin",
  "password": "123"
}
```

### ایجاد آگهی:
```json
POST /api/ads
Authorization: Bearer YOUR_TOKEN
{
  "title": "فروش خودرو",
  "description": "خودرو در حالت عالی",
  "price": 50000000
}
```

### دریافت لیست کاربران (فقط ادمین):
```json
GET /api/acl/users
Authorization: Bearer YOUR_TOKEN
```

## نکات مهم

1. **احراز هویت**: اکثر endpoint ها نیاز به token دارند
2. **مجوزها**: endpoint ها بر اساس نقش کاربر محدود شده‌اند
3. **فرمت پاسخ**: تمام پاسخ‌ها فرمت استاندارد ApiResponse دارند
4. **زبان**: تمام پیام‌ها به فارسی هستند

## کدهای خطای رایج

- `401`: احراز هویت نشده
- `403`: دسترسی مجاز نیست  
- `404`: منبع پیدا نشد
- `400`: خطا در درخواست
- `500`: خطای سرور

## حساب‌های پیش‌فرض

### ادمین سیستم:
- **نام کاربری**: `admin`
- **رمز عبور**: `123`
- **دسترسی**: تمام endpoint ها

### کاربر عادی:
- **نام کاربری**: `user`  
- **رمز عبور**: `123`
- **دسترسی**: endpoint های عمومی

## توسعه و تست

### اضافه کردن مستندات به Controller جدید:

```java
@Tag(name = "نام Controller", description = "توضیحات")
@RestController
@RequestMapping("/api/path")
public class YourController {

    @Operation(summary = "خلاصه", description = "توضیحات کامل")
    @ApiResponse(responseCode = "200", description = "موفق")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Entity>>> getAll() {
        // کد...
    }
}
```

### اضافه کردن Schema به DTO:

```java
@Data
@Schema(description = "توضیحات DTO")
public class YourDto {
    
    @Schema(description = "توضیحات فیلد", example = "مثال", required = true)
    private String field;
}
```

برای اطلاعات کامل، فایل `docs/API_DOCUMENTATION.md` را مطالعه کنید.
