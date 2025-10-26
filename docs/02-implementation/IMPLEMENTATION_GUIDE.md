# راهنمای پیاده‌سازی و استفاده - Iran ECommerce Project

## فهرست مطالب
1. [پیشرفت پیاده‌سازی](#پیشرفت-پیاده‌سازی)
2. [راهنمای استفاده](#راهنمای-استفاده)
3. [مثال‌های عملی](#مثال‌های-عملی)
4. [Testing Guide](#testing-guide)
5. [Troubleshooting](#troubleshooting)
6. [Next Steps](#next-steps)

## پیشرفت پیاده‌سازی

### ✅ **مرحله 1: Authentication/Security - کامل شده**

#### **پیاده‌سازی شده:**
- **✅ CustomUserDetailsService**: احراز هویت پیشرفته با database integration
- **✅ SecurityConfig**: Method-level security با @EnableMethodSecurity
- **✅ CustomPermissionEvaluator**: ارزیابی پیچیده مجوزها
- **✅ JWT Authentication**: Token-based authentication با 10 ساعت انقضا
- **✅ Role-based Authorization**: RBAC کامل
- **✅ Method-level Security**: @PreAuthorize در controllers

#### **Controllers با Security:**
- **✅ UserController**: 15+ endpoint با سطوح مختلف دسترسی
- **✅ AdController**: Role-based و ownership-based authorization
- **✅ AuthController**: Login/Signup endpoints

#### **Database Security:**
- **✅ BCrypt Password Encoding**: Strength 12
- **✅ User Status Management**: ACTIVE, INACTIVE, SUSPENDED, BANNED, DELETED, PENDING_VERIFICATION
- **✅ Role & Permission System**: Dynamic permissions از database

### ✅ **مرحله 2: API Documentation - کامل شده**

#### **Swagger/OpenAPI Integration:**
- **✅ Dependencies**: springdoc-openapi-starter-webmvc-ui/api v2.2.0
- **✅ OpenApiConfig**: تنظیمات کامل با JWT security
- **✅ Security Integration**: Swagger endpoints از authentication معاف
- **✅ UI Configuration**: Enhanced Swagger UI experience

#### **Controllers Documentation:**
- **✅ AuthController**: کامل با @Tag, @Operation, @ApiResponse
- **✅ UserController**: Method-level documentation با security requirements
- **✅ AdController**: Parameter descriptions و response schemas

#### **DTO Schema Documentation:**
- **✅ LoginUserDto**: Schema annotations با examples
- **✅ SignupUserDto**: Validation info در documentation
- **✅ ApiResponse**: استاندارد response format

#### **Configuration:**
- **✅ application.properties**: Swagger UI optimization
- **✅ Security exclusions**: /swagger-ui/**, /v3/api-docs/**

### ✅ **مرحله 3: Exception Handling & Validation - کامل شده**

#### **Global Exception Handler:**
- **✅ Validation Exceptions**: MethodArgumentNotValidException, ConstraintViolationException
- **✅ Security Exceptions**: AuthenticationException, AccessDeniedException
- **✅ Business Exceptions**: Custom exceptions برای منطق کسب‌وکار
- **✅ HTTP Exceptions**: Method not supported, endpoint not found
- **✅ General Exceptions**: Runtime errors با error tracking

#### **Custom Exception Classes:**
- **✅ ResourceNotFoundException**: منبع پیدا نشده
- **✅ UnauthorizedOperationException**: عملیات غیرمجاز
- **✅ ConflictException**: تداخل در عملیات
- **✅ BadRequestException**: درخواست نامعتبر
- **✅ BusinessException**: منطق کسب‌وکار (قبلی)

#### **Validation System:**
- **✅ Bean Validation**: spring-boot-starter-validation
- **✅ Standard Annotations**: @NotBlank, @Size, @Email, @Positive
- **✅ Custom Validation**: @ValidPrice annotation
- **✅ DTO Validation**: LoginUserDto, SignupUserDto, AdRequestDto

#### **ApiResponse Integration:**
- **✅ Standardized Responses**: تمام exceptions از ApiResponse استفاده می‌کنند
- **✅ Error Codes**: استاندارد error codes
- **✅ Persian Messages**: پیام‌های خطا به فارسی

## راهنمای استفاده

### 🚀 **راه‌اندازی پروژه**

#### **1. پیش‌نیازها:**
```bash
# Java 17+
java -version

# PostgreSQL
psql --version

# Maven 3.6+
mvn -version
```

#### **2. تنظیم Database:**
```sql
-- ایجاد دیتابیس
CREATE DATABASE bazaar;

-- تنظیم کاربر (اختیاری)
CREATE USER bazaar_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE bazaar TO bazaar_user;
```

#### **3. تنظیم application.properties:**
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/bazaar
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

# Security Configuration  
app.jwt.secret=mySecretKey
app.jwt.expiration=36000000

# Swagger Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

#### **4. اجرای پروژه:**
```bash
# Clone repository
git clone https://gitlab.com/webrayan/iran-bazaar.git
cd iran-store

# Install dependencies & run
mvn clean install
mvn spring-boot:run
```

#### **5. دسترسی به مستندات:**
- **Application**: http://localhost:8005
- **Swagger UI**: http://localhost:8005/swagger-ui.html
- **API Docs**: http://localhost:8005/v3/api-docs

### 🔐 **استفاده از Authentication**

#### **1. دریافت Token:**
```bash
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "ورود موفق",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### **2. استفاده از Token:**
```bash
curl -X GET http://localhost:8005/api/acl/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### **3. در Swagger UI:**
1. روی دکمه **"Authorize"** کلیک کنید
2. Token را با پیشوند `Bearer ` وارد کنید:
   ```
   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
3. **"Authorize"** کلیک کنید

### 📝 **استفاده از Validation**

#### **1. در DTO Classes:**
```java
@Data
@Schema(description = "درخواست ایجاد محصول")
public class CreateProductDto {
    
    @NotBlank(message = "نام محصول الزامی است")
    @Size(min = 2, max = 100)
    private String name;
    
    @ValidPrice(min = 1000, max = 999999999)
    private Double price;
    
    @Email(message = "فرمت ایمیل صحیح نیست")
    private String email;
}
```

#### **2. در Controllers:**
```java
@PostMapping
public ResponseEntity<ApiResponse<Product>> createProduct(
        @Valid @RequestBody CreateProductDto dto) {
    
    Product product = productService.create(dto);
    return ResponseEntity.ok(ApiResponse.success("محصول ایجاد شد", product));
}
```

#### **3. مدیریت خطاهای Validation:**
خطاهای validation خودکار توسط GlobalExceptionHandler مدیریت می‌شوند:

```json
{
  "success": false,
  "message": "خطا در اعتبارسنجی فیلدها",
  "data": {
    "name": "نام محصول الزامی است",
    "price": "قیمت باید بین 1000 تا 999 میلیون تومان باشد"
  },
  "errorCode": "VALIDATION_ERROR"
}
```

### 🔧 **استفاده از Custom Exceptions**

#### **1. در Service Classes:**
```java
@Service
public class UserService {
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر", "id", id));
    }
    
    public User createUser(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("username", dto.getUsername());
        }
        
        // Business logic
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id, String currentUsername) {
        User user = getUserById(id);
        
        if (!user.getUsername().equals(currentUsername) && !isAdmin(currentUsername)) {
            throw new UnauthorizedOperationException("حذف", "کاربر");
        }
        
        userRepository.delete(user);
    }
}
```

#### **2. Custom Business Validation:**
```java
@Service
public class AdService {
    
    public Ad createAd(AdRequestDto dto, String username) {
        // بررسی مجوز ایجاد آگهی
        if (!canCreateAd(username)) {
            throw new UnauthorizedOperationException("ایجاد", "آگهی");
        }
        
        // بررسی تعداد آگهی‌های فعال
        long activeAds = adRepository.countActiveAdsByUser(username);
        if (activeAds >= MAX_ACTIVE_ADS) {
            throw new BadRequestException("شما نمی‌توانید بیش از " + MAX_ACTIVE_ADS + " آگهی فعال داشته باشید");
        }
        
        return adRepository.save(new Ad(dto));
    }
}
```

## مثال‌های عملی

### 📱 **مثال 1: ایجاد کاربر جدید**

#### **Frontend Request:**
```javascript
// JavaScript/React
const createUser = async (userData) => {
  try {
    const response = await fetch('/api/acl/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(userData)
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('کاربر ایجاد شد:', result.data);
    } else {
      console.error('خطا:', result.message);
    }
  } catch (error) {
    console.error('خطای شبکه:', error);
  }
};
```

#### **Backend Response (Success):**
```json
{
  "success": true,
  "message": "کاربر با موفقیت ایجاد شد",
  "data": {
    "id": 123,
    "username": "john_doe",
    "email": "john@example.com",
    "status": "ACTIVE",
    "roles": ["CUSTOMER"]
  }
}
```

#### **Backend Response (Validation Error):**
```json
{
  "success": false,
  "message": "خطا در اعتبارسنجی فیلدها",
  "data": {
    "username": "نام کاربری باید بین 3 تا 50 کاراکتر باشد",
    "email": "فرمت آدرس ایمیل صحیح نیست"
  },
  "errorCode": "VALIDATION_ERROR"
}
```

### 🛒 **مثال 2: ایجاد آگهی**

#### **cURL Request:**
```bash
curl -X POST http://localhost:8005/api/ads \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "categoryId": 1,
    "title": "فروش خودرو پراید مدل 1400",
    "description": "خودرو در حالت عالی، کم کارکرد، رنگ سفید",
    "price": 45000000
  }'
```

#### **Success Response:**
```json
{
  "success": true,
  "message": "آگهی با موفقیت ایجاد شد",
  "data": {
    "id": 456,
    "title": "فروش خودرو پراید مدل 1400",
    "price": 45000000,
    "status": "PENDING",
    "createdAt": "2025-09-09T10:30:00"
  }
}
```

### 🔍 **مثال 3: جستجوی کاربران (Admin)**

#### **Request:**
```bash
curl -X GET "http://localhost:8005/api/acl/users?page=0&size=10" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### **Response:**
```json
{
  "success": true,
  "message": "لیست کاربران",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "status": "ACTIVE",
      "roles": ["SYSTEM_ADMIN"]
    },
    {
      "id": 2,
      "username": "user1",
      "email": "user1@example.com", 
      "status": "ACTIVE",
      "roles": ["CUSTOMER"]
    }
  ]
}
```

## Testing Guide

### 🧪 **1. تست Validation:**

```java
@Test
public void createUser_WithInvalidData_ReturnsValidationError() {
    // Given
    CreateUserDto dto = new CreateUserDto();
    dto.setUsername("ab"); // کوتاه‌تر از حد مجاز
    dto.setEmail("invalid-email"); // فرمت نامعتبر
    
    // When
    ResponseEntity<ApiResponse> response = userController.createUser(dto);
    
    // Then
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
}
```

### 🧪 **2. تست Authentication:**

```java
@Test
public void getUserById_WithoutToken_ReturnsUnauthorized() {
    // When
    ResponseEntity response = restTemplate.getForEntity("/api/acl/users/1", ApiResponse.class);
    
    // Then
    assertEquals(401, response.getStatusCodeValue());
}

@Test
public void getUserById_WithValidToken_ReturnsUser() {
    // Given
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(validJwtToken);
    HttpEntity entity = new HttpEntity(headers);
    
    // When
    ResponseEntity<ApiResponse> response = restTemplate.exchange(
        "/api/acl/users/1", HttpMethod.GET, entity, ApiResponse.class);
    
    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody().isSuccess());
}
```

### 🧪 **3. تست Custom Exceptions:**

```java
@Test
public void createUser_WithDuplicateUsername_ThrowsConflictException() {
    // Given
    CreateUserDto dto = new CreateUserDto();
    dto.setUsername("existing_user");
    
    when(userRepository.existsByUsername("existing_user")).thenReturn(true);
    
    // When & Then
    ConflictException exception = assertThrows(ConflictException.class, () -> {
        userService.createUser(dto);
    });
    
    assertEquals("تداخل در فیلد 'username' با مقدار 'existing_user'", 
                 exception.getMessage());
}
```

## Troubleshooting

### ❌ **مشکلات رایج و راه‌حل:**

#### **1. خطای 401 - Unauthorized:**
```
مشکل: Token نامعتبر یا منقضی شده
راه‌حل: Token جدید از /api/auth/login دریافت کنید
```

#### **2. خطای 403 - Forbidden:**
```
مشکل: عدم دسترسی کافی
راه‌حل: بررسی role و permissions کاربر
```

#### **3. خطای Validation:**
```
مشکل: داده‌های ورودی نامعتبر
راه‌حل: بررسی فیلدهای اجباری و فرمت داده‌ها
```

#### **4. خطای Circular Dependency:**
```
مشکل: وابستگی دایره‌ای در Spring beans
راه‌حل: کلاس PasswordConfig جداگانه ایجاد شده
```

#### **5. خطای Database Connection:**
```
مشکل: اتصال به PostgreSQL
راه‌حل: بررسی تنظیمات application.properties
```

### 🔧 **ابزارهای Debug:**

#### **1. Logging Configuration:**
```properties
# در application.properties
logging.level.com.webrayan.store=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### **2. Health Check Endpoints:**
```bash
# بررسی وضعیت application
curl http://localhost:8005/actuator/health

# بررسی metrics
curl http://localhost:8005/actuator/metrics
```

#### **3. Database Status:**
```sql
-- بررسی جداول
\dt

-- بررسی کاربران
SELECT * FROM acl_users;

-- بررسی نقش‌ها
SELECT * FROM acl_roles;
```

## Next Steps

### 🎯 **مراحل بعدی توسعه:**

#### **1. Data Transfer Layer:**
- DTO mapping با MapStruct
- Response pagination
- Advanced filtering

#### **2. Caching Strategy:**
- Redis integration
- Cache annotations
- Cache invalidation

#### **3. File Upload System:**
- Image upload برای آگهی‌ها
- File validation
- Storage management

#### **4. Email System:**
- Email verification
- Password reset
- Notification emails

#### **5. Advanced Security:**
- Rate limiting
- IP blocking
- Advanced permission system

#### **6. Performance Optimization:**
- Database indexing
- Query optimization
- Connection pooling

#### **7. Monitoring & Logging:**
- Centralized logging
- Application metrics
- Error tracking

#### **8. Testing Coverage:**
- Integration tests
- Performance tests
- Security tests

### 📋 **Priority List:**

1. **High Priority**: File Upload System
2. **Medium Priority**: Caching Strategy  
3. **Low Priority**: Advanced Monitoring

### 🚀 **Deployment Readiness:**

**✅ Ready for Development Environment**
**⏳ Needs Configuration for Production:**
- Environment-specific configs
- Security hardening
- Performance tuning
- Database migration scripts

---

**پروژه Iran ECommerce** حالا آماده برای توسعه پیشرفته و استفاده در محیط توسعه است! 🎉
