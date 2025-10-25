# Exception Handling & Validation - مستندات کامل

## فهرست مطالب
1. [معرفی](#معرفی)
2. [Global Exception Handler](#global-exception-handler)
3. [Custom Exceptions](#custom-exceptions)
4. [Validation Annotations](#validation-annotations)
5. [Custom Validation](#custom-validation)
6. [مثال‌های عملی](#مثال‌های-عملی)
7. [Best Practices](#best-practices)

## معرفی

سیستم Exception Handling و Validation در پروژه Persia Bazaar برای ارائه تجربه کاربری بهتر و مدیریت یکپارچه خطاها طراحی شده است.

### ویژگی‌های کلیدی:
- **Global Exception Handler**: مدیریت مرکزی تمام خطاها
- **Custom Exceptions**: خطاهای سفارشی برای منطق کسب‌وکار
- **Bean Validation**: اعتبارسنجی خودکار ورودی‌ها
- **Custom Validators**: قوانین اعتبارسنجی سفارشی
- **Standardized Responses**: پاسخ‌های یکسان با کلاس ApiResponse

## Global Exception Handler

### مکان: `core/exception/GlobalExceptionHandler.java`

```java
@Slf4j
@RestControllerAdvice
@Hidden // از Swagger مخفی می‌کند
public class GlobalExceptionHandler {
    // مدیریت تمام خطاها
}
```

### دسته‌بندی خطاها:

#### 1. **Validation Exceptions**
- `MethodArgumentNotValidException` - خطای اعتبارسنجی فیلدها
- `ConstraintViolationException` - خطای محدودیت‌های اعتبارسنجی  
- `MissingServletRequestParameterException` - پارامتر اجباری گم شده
- `MethodArgumentTypeMismatchException` - نوع داده نامناسب
- `HttpMessageNotReadableException` - JSON نامعتبر

#### 2. **Security Exceptions**
- `AuthenticationException` - خطای احراز هویت
- `BadCredentialsException` - اعتبارات نامعتبر
- `AccessDeniedException` - عدم دسترسی

#### 3. **Business Exceptions**
- `BusinessException` - خطاهای منطق کسب‌وکار
- `EntityNotFoundException` - موجودیت پیدا نشده
- `ResourceNotFoundException` - منبع پیدا نشده
- `UnauthorizedOperationException` - عملیات غیرمجاز
- `ConflictException` - تداخل در عملیات
- `BadRequestException` - درخواست نامعتبر

#### 4. **HTTP Exceptions**
- `HttpRequestMethodNotSupportedException` - روش HTTP پشتیبانی نشده
- `NoHandlerFoundException` - Endpoint پیدا نشده

#### 5. **General Exceptions**
- `Exception` - خطاهای عمومی
- `RuntimeException` - خطاهای Runtime

### مثال پاسخ خطا:

```json
{
    "success": false,
    "message": "خطا در اعتبارسنجی فیلدها",
    "data": {
        "username": "نام کاربری نمی‌تواند خالی باشد",
        "password": "رمز عبور باید بین 6 تا 100 کاراکتر باشد"
    },
    "errorCode": "VALIDATION_ERROR"
}
```

## Custom Exceptions

### 1. ResourceNotFoundException
```java
// استفاده
throw new ResourceNotFoundException("کاربر", "id", userId);

// پیام خروجی
"کاربر با id: '123' پیدا نشد"
```

### 2. UnauthorizedOperationException
```java
// استفاده  
throw new UnauthorizedOperationException("ویرایش", "آگهی");

// پیام خروجی
"شما مجوز انجام عملیات 'ویرایش' روی منبع 'آگهی' را ندارید"
```

### 3. ConflictException
```java
// استفاده
throw new ConflictException("email", userEmail);

// پیام خروجی
"تداخل در فیلد 'email' با مقدار 'user@example.com'"
```

### 4. BadRequestException
```java
// استفاده
throw new BadRequestException("قیمت نمی‌تواند منفی باشد", "INVALID_PRICE");
```

### ایجاد Custom Exception جدید:

```java
public class CustomBusinessException extends RuntimeException {
    private final String errorCode;
    
    public CustomBusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    // getters...
}
```

و اضافه کردن به GlobalExceptionHandler:

```java
@ExceptionHandler(CustomBusinessException.class)
public ResponseEntity<ApiResponse<Void>> handleCustomBusiness(
        CustomBusinessException ex) {
    
    log.warn("خطای کسب‌وکار سفارشی: {}", ex.getMessage());
    
    return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
}
```

## Validation Annotations

### Standard Annotations:

#### String Validation:
```java
@NotBlank(message = "نام کاربری نمی‌تواند خالی باشد")
@Size(min = 3, max = 50, message = "نام کاربری باید بین 3 تا 50 کاراکتر باشد")
@Email(message = "فرمت آدرس ایمیل صحیح نیست")
@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "نام کاربری فقط شامل حروف، اعداد و _ باشد")
private String username;
```

#### Number Validation:
```java
@NotNull(message = "قیمت نمی‌تواند خالی باشد")
@Positive(message = "قیمت باید عدد مثبت باشد")
@Min(value = 1000, message = "حداقل قیمت 1000 تومان است")
@Max(value = 999999999, message = "حداکثر قیمت 999 میلیون تومان است")
@DecimalMax(value = "999999999.99", message = "قیمت بیش از حد مجاز است")
private Double price;
```

#### Date Validation:
```java
@Past(message = "تاریخ تولد باید در گذشته باشد")
@DateTimeFormat(pattern = "yyyy-MM-dd")
private LocalDate birthDate;

@Future(message = "تاریخ انقضا باید در آینده باشد")
private LocalDateTime expiryDate;
```

#### Collection Validation:
```java
@NotEmpty(message = "لیست دسته‌بندی‌ها نمی‌تواند خالی باشد")
@Size(min = 1, max = 5, message = "حداکثر 5 دسته‌بندی انتخاب کنید")
private List<Long> categoryIds;
```

### مثال DTO کامل:

```java
@Data
@Schema(description = "درخواست ایجاد کاربر")
public class CreateUserDto {
    
    @Schema(description = "نام کاربری", required = true)
    @NotBlank(message = "نام کاربری الزامی است")
    @Size(min = 3, max = 50, message = "نام کاربری باید بین 3 تا 50 کاراکتر باشد")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "نام کاربری فقط شامل حروف، اعداد و _ باشد")
    private String username;
    
    @Schema(description = "آدرس ایمیل", required = true)
    @NotBlank(message = "ایمیل الزامی است")
    @Email(message = "فرمت ایمیل صحیح نیست")
    private String email;
    
    @Schema(description = "رمز عبور", required = true)
    @NotBlank(message = "رمز عبور الزامی است")
    @Size(min = 6, max = 100, message = "رمز عبور باید بین 6 تا 100 کاراکتر باشد")
    private String password;
    
    @Schema(description = "شماره تلفن")
    @Pattern(regexp = "^09\\d{9}$", message = "شماره تلفن باید با 09 شروع شده و 11 رقم باشد")
    private String phoneNumber;
    
    @Schema(description = "سن")
    @Min(value = 18, message = "حداقل سن 18 سال است")
    @Max(value = 100, message = "حداکثر سن 100 سال است")
    private Integer age;
}
```

## Custom Validation

### 1. ایجاد Custom Annotation:

```java
@Documented
@Constraint(validatedBy = ValidPriceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    String message() default "قیمت نامعتبر است";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    double min() default 0.0;
    double max() default Double.MAX_VALUE;
    boolean allowZero() default false;
}
```

### 2. ایجاد Validator:

```java
public class ValidPriceValidator implements ConstraintValidator<ValidPrice, Double> {
    private double min;
    private double max;
    private boolean allowZero;
    
    @Override
    public void initialize(ValidPrice constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.allowZero = constraintAnnotation.allowZero();
    }
    
    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        if (value == 0.0 && !allowZero) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("قیمت نمی‌تواند صفر باشد")
                   .addConstraintViolation();
            return false;
        }
        
        if (value < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("قیمت نمی‌تواند منفی باشد")
                   .addConstraintViolation();
            return false;
        }
        
        return value >= min && value <= max;
    }
}
```

### 3. استفاده از Custom Validation:

```java
@ValidPrice(min = 1000, max = 999999999, message = "قیمت باید بین 1000 تا 999 میلیون تومان باشد")
private Double price;
```

### مثال‌های Custom Validation دیگر:

#### تاریخ تولد معتبر:
```java
@ValidBirthDate
private LocalDate birthDate;
```

#### شماره ملی ایرانی:
```java
@ValidNationalCode
private String nationalCode;
```

#### کد پستی ایرانی:
```java
@ValidPostalCode
private String postalCode;
```

## مثال‌های عملی

### 1. Controller با Validation:

```java
@PostMapping
public ResponseEntity<ApiResponse<User>> createUser(
        @Valid @RequestBody CreateUserDto dto) {
    try {
        User user = userService.createUser(dto);
        return ResponseEntity.ok(ApiResponse.success("کاربر ایجاد شد", user));
    } catch (ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage(), "USER_CONFLICT"));
    }
}
```

### 2. Service با Exception Handling:

```java
@Service
public class UserService {
    
    public User createUser(CreateUserDto dto) {
        // بررسی وجود کاربر
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("username", dto.getUsername());
        }
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("email", dto.getEmail());
        }
        
        // ایجاد کاربر
        User user = new User();
        // ...
        
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("خطا در ذخیره کاربر: " + e.getMessage());
        }
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر", "id", id));
    }
}
```

### 3. مثال کامل با تمام امکانات:

```java
@Data
@Schema(description = "درخواست ایجاد محصول")
public class CreateProductDto {
    
    @NotBlank(message = "نام محصول الزامی است")
    @Size(min = 2, max = 100, message = "نام محصول باید بین 2 تا 100 کاراکتر باشد")
    private String name;
    
    @Size(max = 1000, message = "توضیحات نمی‌تواند بیش از 1000 کاراکتر باشد")
    private String description;
    
    @ValidPrice(min = 1000, max = 999999999)
    private Double price;
    
    @NotNull(message = "دسته‌بندی الزامی است")
    @Positive(message = "شناسه دسته‌بندی نامعتبر است")
    private Long categoryId;
    
    @NotEmpty(message = "حداقل یک تگ انتخاب کنید")
    @Size(max = 10, message = "حداکثر 10 تگ مجاز است")
    private List<String> tags;
    
    @ValidImageUrl
    private String imageUrl;
}
```

## Best Practices

### 1. **Message استاندارد**:
```java
// بد
@NotBlank(message = "Name cannot be empty")

// خوب  
@NotBlank(message = "نام محصول نمی‌تواند خالی باشد")
```

### 2. **Logging مناسب**:
```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
    // Log با سطح مناسب
    log.warn("خطای منطق کسب‌وکار: {}", ex.getMessage());
    
    // جزئیات بیشتر در debug mode
    log.debug("Stack trace:", ex);
    
    return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.getMessage(), "BUSINESS_ERROR"));
}
```

### 3. **Error Codes استاندارد**:
```java
public class ErrorCodes {
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String UNAUTHORIZED_OPERATION = "UNAUTHORIZED_OPERATION";
    public static final String DUPLICATE_ENTRY = "DUPLICATE_ENTRY";
    // ...
}
```

### 4. **Group Validation**:
```java
public interface CreateGroup {}
public interface UpdateGroup {}

public class UserDto {
    @NotNull(groups = CreateGroup.class)
    @Null(groups = UpdateGroup.class)
    private Long id;
    
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String username;
}

// در Controller
@PostMapping
public ResponseEntity<?> create(@Validated(CreateGroup.class) @RequestBody UserDto dto) {
    // ...
}
```

### 5. **Custom Error Messages**:
```java
# در messages.properties
validation.username.required=نام کاربری الزامی است
validation.username.size=نام کاربری باید بین {min} تا {max} کاراکتر باشد
validation.email.invalid=فرمت ایمیل صحیح نیست
```

### 6. **Exception Documentation**:
```java
/**
 * سرویس مدیریت کاربران
 * 
 * @throws ResourceNotFoundException اگر کاربر پیدا نشود
 * @throws ConflictException اگر نام کاربری یا ایمیل تکراری باشد
 * @throws UnauthorizedOperationException اگر دسترسی کافی نباشد
 */
@Service
public class UserService {
    // ...
}
```

### 7. **Testing Exceptions**:
```java
@Test
public void createUser_WhenUsernameExists_ThrowsConflictException() {
    // Given
    CreateUserDto dto = new CreateUserDto();
    dto.setUsername("existing_user");
    
    when(userRepository.existsByUsername("existing_user")).thenReturn(true);
    
    // When & Then
    assertThrows(ConflictException.class, () -> {
        userService.createUser(dto);
    });
}
```

این سیستم Exception Handling و Validation امکان مدیریت حرفه‌ای خطاها و اعتبارسنجی دقیق ورودی‌ها را فراهم می‌کند و تجربه کاربری بهتری ارائه می‌دهد.
