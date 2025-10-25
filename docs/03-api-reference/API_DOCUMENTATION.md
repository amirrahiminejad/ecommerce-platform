# مستندات API - Swagger/OpenAPI

## فهرست مطالب
1. [معرفی](#معرفی)
2. [نصب و راه‌اندازی](#نصب-و-راه‌اندازی)
3. [تنظیمات پروژه](#تنظیمات-پروژه)
4. [استفاده از Annotations](#استفاده-از-annotations)
5. [کلاس ApiResponse](#کلاس-apiresponse)
6. [مثال‌های عملی](#مثال‌های-عملی)
7. [دسترسی به مستندات](#دسترسی-به-مستندات)

## معرفی

Swagger/OpenAPI یک استاندارد برای مستندسازی REST API ها است که امکانات زیر را فراهم می‌کند:

- **مستندسازی خودکار**: تولید مستندات از روی کد
- **UI تعاملی**: امکان تست API ها از طریق رابط وب
- **استانداردسازی**: فرمت یکسان برای توصیف API ها
- **تولید کد**: امکان تولید Client SDK ها
- **اعتبارسنجی**: بررسی صحت درخواست‌ها و پاسخ‌ها

## نصب و راه‌اندازی

### Dependencies در pom.xml

```xml
<!-- Swagger/OpenAPI Documentation -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.2.0</version>
</dependency>
```

### تنظیمات Application Properties

```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
```

## تنظیمات پروژه

### OpenApiConfig کلاس

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServersList())
                .components(createComponents())
                .addSecurityItem(createSecurityRequirement());
    }
    
    // سایر تنظیمات...
}
```

### تنظیمات Security

در `SecurityConfig` باید endpoint های Swagger را از احراز هویت مستثنی کنیم:

```java
.requestMatchers("/swagger-ui/**").permitAll()
.requestMatchers("/swagger-ui.html").permitAll()
.requestMatchers("/v3/api-docs/**").permitAll()
.requestMatchers("/swagger-resources/**").permitAll()
.requestMatchers("/webjars/**").permitAll()
```

## استفاده از Annotations

### Controller Level Annotations

```java
@Tag(name = "Authentication", description = "API های مربوط به احراز هویت")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // ...
}
```

### Method Level Annotations

```java
@Operation(
    summary = "ورود کاربر", 
    description = "احراز هویت کاربر و دریافت JWT token"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200", 
        description = "ورود موفق",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                    "success": true
                }
                """
            )
        )
    ),
    @ApiResponse(
        responseCode = "401", 
        description = "نام کاربری یا رمز عبور اشتباه"
    )
})
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginUserDto login) {
    // ...
}
```

### Parameter Annotations

```java
public ResponseEntity<?> getUserById(
    @Parameter(description = "شناسه کاربر", required = true) 
    @PathVariable Long id) {
    // ...
}
```

### DTO Schema Annotations

```java
@Data
@Schema(description = "اطلاعات ورود کاربر")
public class LoginUserDto {
    
    @Schema(
        description = "نام کاربری یا آدرس ایمیل", 
        example = "admin",
        required = true
    )
    private String username;
    
    @Schema(
        description = "رمز عبور", 
        example = "123",
        required = true
    )
    private String password;
}
```

## کلاس ApiResponse

### هدف و مزایا

کلاس `ApiResponse` برای استانداردسازی پاسخ‌های API طراحی شده است:

```java
@Data
@Schema(description = "پاسخ استاندارد API")
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    
    // Static factory methods
    public static <T> ApiResponse<T> success(T data) { /* ... */ }
    public static <T> ApiResponse<T> error(String message) { /* ... */ }
}
```

### مزایا:

1. **استانداردسازی**: همه پاسخ‌ها فرمت یکسانی دارند
2. **مستندسازی بهتر**: Swagger می‌تواند ساختار پاسخ را بهتر نمایش دهد
3. **مدیریت خطاها**: ساختار ثابت برای نمایش خطاها
4. **سازگاری Frontend**: Frontend همیشه می‌داند چه ساختاری انتظار داشته باشد

### قبل از استفاده از ApiResponse:

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginUserDto login) {
    Map<String, Object> response = new HashMap<>();
    try {
        // احراز هویت
        response.put("token", token);
        response.put("success", true);
        return ResponseEntity.ok(response);
    } catch (AuthenticationException e) {
        response.put("message", "Invalid username or password");
        response.put("success", false);
        return ResponseEntity.status(401).body(response);
    }
}
```

### بعد از استفاده از ApiResponse:

```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginUserDto login) {
    try {
        // احراز هویت
        Map<String, String> tokenData = Map.of("token", token);
        return ResponseEntity.ok(ApiResponse.success("ورود موفق", tokenData));
    } catch (AuthenticationException e) {
        return ResponseEntity.status(401)
            .body(ApiResponse.error("نام کاربری یا رمز عبور اشتباه", "AUTH_FAILED"));
    }
}
```

## مثال‌های عملی

### 1. Controller کامل با Swagger

```java
@Tag(name = "User Management", description = "مدیریت کاربران")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Operation(summary = "دریافت لیست کاربران")
    @ApiResponse(responseCode = "200", description = "لیست کاربران")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("لیست کاربران", users));
    }

    @Operation(summary = "ایجاد کاربر جدید")
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(
        @Valid @RequestBody CreateUserDto dto) {
        User user = userService.createUser(dto);
        return ResponseEntity.ok(ApiResponse.success("کاربر ایجاد شد", user));
    }
}
```

### 2. مدیریت خطاها

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("خطا در اعتبارسنجی: " + e.getMessage(), "VALIDATION_ERROR"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(404)
            .body(ApiResponse.error("موجودیت پیدا نشد", "NOT_FOUND"));
    }
}
```

### 3. DTO با Schema کامل

```java
@Data
@Schema(description = "درخواست ایجاد کاربر")
public class CreateUserDto {
    
    @Schema(description = "نام کاربری", example = "john_doe", required = true)
    @NotBlank(message = "نام کاربری الزامی است")
    private String username;
    
    @Schema(description = "آدرس ایمیل", example = "john@example.com", required = true)
    @Email(message = "فرمت ایمیل صحیح نیست")
    private String email;
    
    @Schema(description = "رمز عبور", example = "password123", required = true, minLength = 6)
    @Size(min = 6, message = "رمز عبور باید حداقل 6 کاراکتر باشد")
    private String password;
}
```

## دسترسی به مستندات

پس از راه‌اندازی، می‌توانید به مستندات API از طریق آدرس‌های زیر دسترسی پیدا کنید:

### Swagger UI (رابط تعاملی):
```
http://localhost:8005/swagger-ui.html
```

### API Documentation (JSON):
```
http://localhost:8005/v3/api-docs
```

### مثال‌های Authentication در Swagger:

1. ابتدا از endpoint `/api/auth/login` برای دریافت token استفاده کنید
2. token دریافتی را در قسمت "Authorize" وارد کنید
3. برای تمام درخواست‌های بعدی از این token استفاده می‌شود

## نکات مهم

### 1. Security در Swagger
- همیشه endpoint های مربوط به Swagger را از security exclude کنید
- برای API های محافظت شده از `@SecurityRequirement` استفاده کنید

### 2. بهینه‌سازی Performance
- از `@Hidden` برای پنهان کردن endpoint های داخلی استفاده کنید
- گروه‌بندی endpoint ها با `@Tag` برای سازماندهی بهتر

### 3. مستندسازی کامل
- همیشه `description` و `example` برای تمام فیلدها تعریف کنید
- کدهای response مختلف را مستند کنید
- از `@ApiResponse` برای تعریف پاسخ‌های مختلف استفاده کنید

### 4. استفاده از ApiResponse
- برای یکسان‌سازی تمام پاسخ‌ها از کلاس `ApiResponse` استفاده کنید
- کدهای خطای استاندارد تعریف کنید
- پیام‌های خطا را به زبان فارسی ارائه دهید

## مثال کامل یک Endpoint

```java
@Operation(
    summary = "بروزرسانی اطلاعات کاربر",
    description = "بروزرسانی اطلاعات پروفایل کاربر احراز هویت شده"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "اطلاعات با موفقیت بروزرسانی شد",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ApiResponse.class)
        )
    ),
    @ApiResponse(responseCode = "400", description = "خطا در اعتبارسنجی"),
    @ApiResponse(responseCode = "401", description = "احراز هویت نشده"),
    @ApiResponse(responseCode = "404", description = "کاربر پیدا نشد")
})
@PutMapping("/{id}")
@PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
public ResponseEntity<ApiResponse<User>> updateUser(
    @Parameter(description = "شناسه کاربر", required = true)
    @PathVariable Long id,
    
    @Parameter(description = "اطلاعات جدید کاربر", required = true)
    @Valid @RequestBody UpdateUserDto dto) {
    
    try {
        User updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(
            ApiResponse.success("اطلاعات کاربر بروزرسانی شد", updatedUser)
        );
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(404)
            .body(ApiResponse.error("کاربر پیدا نشد", "USER_NOT_FOUND"));
    } catch (ValidationException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("خطا در اعتبارسنجی: " + e.getMessage(), "VALIDATION_ERROR"));
    }
}
```

این مستندات نشان می‌دهد که چگونه یک API کامل و استاندارد با Swagger/OpenAPI و کلاس ApiResponse ایجاد کنیم که هم برای توسعه‌دهندگان قابل فهم باشد و هم برای کاربران نهایی.
