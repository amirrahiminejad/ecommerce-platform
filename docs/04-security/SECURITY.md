# 🔒 مستندات سیستم امنیت Persia Bazaar

## فهرست مطالب
1. [معرفی کلی](#معرفی-کلی)
2. [معماری امنیت](#معماری-امنیت)
3. [احراز هویت (Authentication)](#احراز-هویت-authentication)
4. [مجوزدهی (Authorization)](#مجوزدهی-authorization)
5. [کنترلرهای امن](#کنترلرهای-امن)
6. [نحوه استفاده](#نحوه-استفاده)
7. [مثال‌های عملی](#مثالهای-عملی)
8. [عیب‌یابی](#عیبیابی)

---

## معرفی کلی

سیستم امنیت Persia Bazaar بر اساس **Spring Security** و **JWT** پیاده‌سازی شده است. این سیستم شامل:

- 🔐 **احراز هویت با JWT**
- 👥 **مدیریت نقش‌ها (Roles)**
- 🛡️ **مدیریت مجوزها (Permissions)**
- 🎯 **امنیت سطح متد (@PreAuthorize)**
- 🔍 **بررسی مالکیت (Ownership Validation)**

---

## معماری امنیت

### ساختار کلی
```
📁 core/security/
├── 🔑 JwtTokenProvider.java          # تولید و اعتبارسنجی JWT
├── 🛡️ JwtAuthenticationFilter.java   # فیلتر احراز هویت
├── 👤 CustomUserDetailsService.java  # بارگذاری اطلاعات کاربر
└── 🎯 CustomPermissionEvaluator.java # ارزیابی مجوزها

📁 core/config/
└── ⚙️ SecurityConfig.java            # پیکربندی امنیت

📁 modules/acl/
├── 👥 entity/User.java
├── 🎭 entity/Role.java
├── 📋 entity/Permission.java
├── 🔧 service/UserService.java
└── 🎮 controller/UserController.java
```

### مدل داده‌ای امنیت

#### User (کاربر)
```java
- id: Long
- username: String
- email: String  
- password: String (هش شده)
- roles: Set<Role>
- status: UserStatus (ACTIVE, INACTIVE, BANNED)
- isActive: Boolean
- emailVerified: Boolean
- phoneVerified: Boolean
```

#### Role (نقش)
```java
- id: Long
- roleName: RoleName (SYSTEM_ADMIN, CUSTOMER, SELLER, AFFILIATE)
- displayName: String
- permissions: Set<Permission>
- isActive: Boolean
```

#### Permission (مجوز)
```java
- id: Long
- permissionName: PermissionName
- resource: String (USER, PRODUCT, ORDER, etc.)
- action: String (CREATE, READ, UPDATE, DELETE)
- isActive: Boolean
```

---

## احراز هویت (Authentication)

### JWT Token Structure
```json
{
  "sub": "username",
  "iat": 1625000000,
  "exp": 1625036000,
  "authorities": [
    "ROLE_CUSTOMER",
    "USER:READ",
    "PRODUCT:CREATE"
  ]
}
```

### CustomUserDetailsService
کلاس `CustomUserDetailsService` اطلاعات کاربر را از دیتابیس بارگذاری می‌کند:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        // جستجو با username یا email
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر پیدا نشد"));
        
        return createUserPrincipal(user);
    }
    
    private UserDetails createUserPrincipal(User user) {
        Collection<GrantedAuthority> authorities = getUserAuthorities(user);
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.getStatus() == User.UserStatus.BANNED)
                .disabled(!user.getIsActive())
                .build();
    }
}
```

### Endpoint های احراز هویت

#### ورود (Login)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123"
}
```

**پاسخ موفق:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### ثبت‌نام (Signup)
```http
POST /api/auth/signup
Content-Type: application/json

{
  "name": "احمد محمدی",
  "email": "ahmad@example.com",
  "password": "MySecurePass123"
}
```

---

## مجوزدهی (Authorization)

### نقش‌های پیش‌فرض

#### SYSTEM_ADMIN
- **توضیح**: مدیر کل سیستم
- **مجوزها**: دسترسی کامل به همه بخش‌ها
- **Authorities**: 
  ```
  ROLE_SYSTEM_ADMIN
  USER:CREATE, USER:READ, USER:UPDATE, USER:DELETE, USER:MANAGE_ROLES
  PRODUCT:CREATE, PRODUCT:READ, PRODUCT:UPDATE, PRODUCT:DELETE
  ORDER:READ, ORDER:UPDATE, ORDER:CANCEL
  SYSTEM:CONFIG, SYSTEM:BACKUP, SYSTEM:MAINTENANCE
  REPORT:SALES, REPORT:FINANCIAL, REPORT:USER
  ```

#### CUSTOMER
- **توضیح**: مشتری عادی
- **مجوزها**: خرید، مدیریت پروفایل شخصی
- **Authorities**:
  ```
  ROLE_CUSTOMER
  ORDER:CREATE, ORDER:READ (own)
  CART:MANAGE (own)
  PROFILE:UPDATE (own)
  ```

#### SELLER
- **توضیح**: فروشنده
- **مجوزها**: مدیریت محصولات و فروش
- **Authorities**:
  ```
  ROLE_SELLER
  PRODUCT:CREATE, PRODUCT:READ, PRODUCT:UPDATE (own)
  ORDER:READ (own shop)
  SHOP:MANAGE (own)
  ```

### سطوح امنیت

#### 1. URL-Level Security (SecurityConfig)
```java
.authorizeHttpRequests(auth -> auth
    // Public endpoints
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/common/countries/**").permitAll()
    
    // Admin endpoints
    .requestMatchers("/api/acl/users/**").hasRole("SYSTEM_ADMIN")
    .requestMatchers("/api/acl/roles/**").hasRole("SYSTEM_ADMIN")
    
    // Seller endpoints
    .requestMatchers("/api/catalog/shops/**").hasAnyRole("SELLER", "SYSTEM_ADMIN")
    .requestMatchers("/api/catalog/products/**").hasAnyRole("SELLER", "SYSTEM_ADMIN")
    
    // Customer endpoints
    .requestMatchers("/api/sales/carts/**").hasAnyRole("CUSTOMER", "SELLER", "SYSTEM_ADMIN")
    .requestMatchers("/api/sales/orders/**").hasAnyRole("CUSTOMER", "SELLER", "SYSTEM_ADMIN")
    
    .anyRequest().authenticated()
)
```

#### 2. Method-Level Security (@PreAuthorize)

**نقش-محور:**
```java
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public ResponseEntity<List<User>> getAllUsers() { ... }
```

**مجوز-محور:**
```java
@PreAuthorize("hasAuthority('USER:CREATE')")
public ResponseEntity<User> createUser(...) { ... }
```

**مالکیت-محور:**
```java
@PreAuthorize("#userId == authentication.principal.id")
public ResponseEntity<User> updateProfile(@PathVariable Long userId, ...) { ... }
```

**ترکیبی:**
```java
@PreAuthorize("hasRole('SYSTEM_ADMIN') or #customerId == authentication.principal.id")
public ResponseEntity<Order> getOrder(@PathVariable Long customerId, ...) { ... }
```

---

## کنترلرهای امن

### UserController
**مسیر**: `/api/acl/users`

| HTTP Method | Endpoint | Security | توضیح |
|-------------|----------|----------|-------|
| GET | `/` | `hasRole('SYSTEM_ADMIN')` | لیست همه کاربران |
| GET | `/{id}` | `hasRole('SYSTEM_ADMIN') or own user` | اطلاعات کاربر |
| POST | `/` | `hasRole('SYSTEM_ADMIN')` | ایجاد کاربر جدید |
| PUT | `/{id}` | `hasRole('SYSTEM_ADMIN') or own user` | به‌روزرسانی کاربر |
| POST | `/{id}/roles` | `hasRole('SYSTEM_ADMIN')` | تخصیص نقش‌ها |
| POST | `/{id}/change-password` | `own user or hasRole('SYSTEM_ADMIN')` | تغییر رمز عبور |

### CategoryController  
**مسیر**: `/api/catalog/categories`

| HTTP Method | Endpoint | Security | توضیح |
|-------------|----------|----------|-------|
| GET | `/` | `Public` | لیست دسته‌بندی‌ها |
| POST | `/` | `hasAuthority('PRODUCT:CREATE')` | ایجاد دسته‌بندی |
| PUT | `/{id}` | `hasAuthority('PRODUCT:UPDATE')` | ویرایش دسته‌بندی |
| DELETE | `/{id}` | `hasAuthority('PRODUCT:DELETE')` | حذف دسته‌بندی |

### AdController
**مسیر**: `/api/ads`

| HTTP Method | Endpoint | Security | توضیح |
|-------------|----------|----------|-------|
| GET | `/` | `Public` | لیست آگهی‌ها |
| POST | `/` | `hasAnyRole('CUSTOMER', 'SELLER')` | ایجاد آگهی |
| PUT | `/{id}` | `hasRole('SYSTEM_ADMIN') or owner` | ویرایش آگهی |
| DELETE | `/{id}` | `hasRole('SYSTEM_ADMIN') or owner` | حذف آگهی |
| PATCH | `/{id}/status` | `hasRole('SYSTEM_ADMIN')` | تغییر وضعیت |

### OrderController
**مسیر**: `/api/sale/orders`

| HTTP Method | Endpoint | Security | توضیح |
|-------------|----------|----------|-------|
| POST | `/create-from-cart` | `authenticated and own customer` | ایجاد سفارش |
| GET | `/{id}` | `hasRole('SYSTEM_ADMIN') or owner` | مشاهده سفارش |
| GET | `/customer/{customerId}` | `hasRole('SYSTEM_ADMIN') or own customer` | سفارش‌های مشتری |

---

## نحوه استفاده

### 1. دریافت Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123"
  }'
```

### 2. استفاده از Token
```bash
curl -X GET http://localhost:8080/api/acl/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. تست مجوزها
```bash
# تست دسترسی ادمین
curl -X POST http://localhost:8080/api/acl/users \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "new@example.com",
    "password": "password123"
  }'

# تست دسترسی کاربر عادی (باید 403 برگرداند)
curl -X POST http://localhost:8080/api/acl/users \
  -H "Authorization: Bearer USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ ... }'
```

---

## مثال‌های عملی

### مثال 1: مدیریت کاربران
```java
// فقط ادمین می‌تواند همه کاربران را ببیند
@GetMapping
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public ResponseEntity<List<User>> getAllUsers() {
    return createSuccessResponse(userService.getAllUsers());
}

// کاربر می‌تواند فقط پروفایل خودش را ببیند
@GetMapping("/{id}")
@PreAuthorize("hasRole('SYSTEM_ADMIN') or #id == authentication.principal.id")
public ResponseEntity<User> getUserById(@PathVariable Long id) {
    return userService.getUserById(id)
            .map(this::createSuccessResponse)
            .orElse(createNotFoundResponse("کاربر پیدا نشد"));
}
```

### مثال 2: مدیریت محصولات
```java
// فقط فروشندگان و ادمین می‌توانند محصول ایجاد کنند
@PostMapping
@PreAuthorize("hasAnyRole('SELLER', 'SYSTEM_ADMIN')")
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    return ResponseEntity.ok(productService.createProduct(product));
}

// فقط مالک محصول یا ادمین می‌تواند ویرایش کند
@PutMapping("/{id}")
@PreAuthorize("hasRole('SYSTEM_ADMIN') or @productService.isProductOwner(#id, authentication.name)")
public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
    return ResponseEntity.ok(productService.updateProduct(id, product));
}
```

### مثال 3: مدیریت سفارشات
```java
// فقط مشتری مالک سفارش یا ادمین می‌تواند ببیند
@GetMapping("/{id}")
@PreAuthorize("hasRole('SYSTEM_ADMIN') or @orderService.isOrderOwner(#id, authentication.name)")
public ResponseEntity<Order> getOrder(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
}

// مشتری فقط می‌تواند سفارش برای خودش ایجاد کند
@PostMapping("/create-from-cart")
@PreAuthorize("#customerId == authentication.principal.id")
public ResponseEntity<Order> createOrder(@RequestParam Long customerId, ...) {
    return ResponseEntity.ok(orderService.createOrderFromCart(customerId, ...));
}
```

---

## عیب‌یابی

### مشکلات رایج

#### 1. Token منقضی شده
**خطا**: `401 Unauthorized`
**راه‌حل**: Token جدید دریافت کنید

#### 2. دسترسی غیرمجاز
**خطا**: `403 Forbidden`
**راه‌حل**: بررسی کنید کاربر نقش مناسب را دارد

#### 3. @PreAuthorize کار نمی‌کند
**راه‌حل**: 
- بررسی کنید `@EnableMethodSecurity` فعال باشد
- کلاس Controller باید managed bean باشد
- Method باید public باشد

#### 4. Custom Permission Evaluator
برای بررسی مجوزهای پیچیده:
```java
@PreAuthorize("hasPermission(#targetId, 'ORDER', 'READ')")
public ResponseEntity<Order> getOrder(@PathVariable Long targetId) { ... }
```

### لاگ‌ها
برای debug کردن، سطح لاگ security را فعال کنید:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.webrayan.bazaar.core.security=DEBUG
```

### تست‌های امنیت
```java
@Test
@WithMockUser(roles = "SYSTEM_ADMIN")
void testAdminCanAccessUsers() {
    // تست دسترسی ادمین
}

@Test
@WithMockUser(roles = "CUSTOMER")
void testCustomerCannotAccessAdminPanel() {
    // تست عدم دسترسی کاربر عادی
}
```

---

## نکات امنیتی مهم

### 1. Password Security
- حداقل 8 کاراکتر
- ترکیبی از حروف بزرگ، کوچک، عدد و نماد
- استفاده از `BCryptPasswordEncoder` با strength 12

### 2. JWT Security
- Secret key قوی و منحصر به فرد
- انقضای مناسب (10 ساعت)
- Refresh token برای امنیت بیشتر

### 3. CORS Security
- فقط دامنه‌های مجاز
- Credentials فقط برای دامنه‌های امن

### 4. Headers Security
- Frame Options: DENY
- Content Type Options: nosniff
- HSTS: فعال
- Referrer Policy: strict-origin-when-cross-origin

---

*آخرین به‌روزرسانی: 7 سپتامبر 2025*
*نسخه: 1.0.0*
