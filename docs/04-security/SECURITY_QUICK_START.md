# 🚀 راهنمای سریع امنیت

## نصب و راه‌اندازی

### 1. کاربر پیش‌فرض
```
Username: admin
Password: 123
Role: SYSTEM_ADMIN
```

### 2. دریافت Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123"}'
```

### 3. استفاده از Token
```bash
curl -X GET http://localhost:8080/api/acl/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## نقش‌ها و مجوزها

| نقش | توضیح | مجوزهای کلیدی |
|-----|-------|----------------|
| `SYSTEM_ADMIN` | مدیر کل | همه مجوزها |
| `CUSTOMER` | مشتری | خرید، پروفایل شخصی |
| `SELLER` | فروشنده | مدیریت محصولات |
| `AFFILIATE` | بازاریاب | گزارشات فروش |

## Endpoints امن

### کاربران (`/api/acl/users`)
- `GET /` - لیست کاربران (فقط ادمین)
- `POST /` - ایجاد کاربر (فقط ادمین)
- `GET /{id}` - پروفایل (ادمین یا خود کاربر)
- `PUT /{id}` - ویرایش (ادمین یا خود کاربر)

### دسته‌بندی (`/api/catalog/categories`)
- `GET /` - عمومی
- `POST /` - ایجاد (ادمین)
- `PUT /{id}` - ویرایش (ادمین)
- `DELETE /{id}` - حذف (ادمین)

### آگهی‌ها (`/api/ads`)
- `GET /` - عمومی
- `POST /` - ایجاد (کاربر وارد شده)
- `PUT /{id}` - ویرایش (مالک یا ادمین)
- `DELETE /{id}` - حذف (مالک یا ادمین)

### سفارشات (`/api/sale/orders`)
- `POST /create-from-cart` - ایجاد (مالک)
- `GET /{id}` - مشاهده (مالک یا ادمین)
- `GET /customer/{customerId}` - سفارشات مشتری (مالک یا ادمین)

## مثال‌های @PreAuthorize

```java
// فقط ادمین
@PreAuthorize("hasRole('SYSTEM_ADMIN')")

// ادمین یا مالک
@PreAuthorize("hasRole('SYSTEM_ADMIN') or #id == authentication.principal.id")

// چند نقش
@PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'SYSTEM_ADMIN')")

// مجوز خاص
@PreAuthorize("hasAuthority('USER:CREATE')")

// شرط پیچیده
@PreAuthorize("hasRole('SYSTEM_ADMIN') or @orderService.isOrderOwner(#id, authentication.name)")
```

## کدهای خطای رایج

- `401 Unauthorized` - Token نامعتبر یا منقضی
- `403 Forbidden` - عدم دسترسی کافی
- `400 Bad Request` - اطلاعات ناقص
- `404 Not Found` - منبع پیدا نشد

## نکات مهم

1. **همیشه Token را در Header قرار دهید:**
   ```
   Authorization: Bearer YOUR_JWT_TOKEN
   ```

2. **برای تست از Postman استفاده کنید:**
   - Collection در `docs/postman_collections/` موجود است

3. **Environment Variables:**
   ```
   JWT_SECRET=your-super-secret-key
   JWT_EXPIRATION=36000000
   ```

4. **CORS برای Frontend:**
   ```javascript
   axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
   ```

## لینک‌های مفید

- [مستندات کامل امنیت](./SECURITY.md)
- [Postman Collections](./postman_collections/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
