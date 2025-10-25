# File Upload System - Quick Start Guide

## فهرست مطالب

1. [نصب و راه‌اندازی اولیه](#نصب-و-راه‌اندازی-اولیه)
2. [پیکربندی سریع](#پیکربندی-سریع)
3. [استفاده از APIها](#استفاده-از-apiها)
4. [نمونه کدهای آماده](#نمونه-کدهای-آماده)
5. [تست و عیب‌یابی](#تست-و-عیب‌یابی)

## نصب و راه‌اندازی اولیه

### گام 1: وابستگی‌ها

```xml
<!-- در pom.xml اضافه کنید -->
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.19</version>
</dependency>
```

### گام 2: تنظیمات application.properties

```properties
# حداقل تنظیمات مورد نیاز
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

app.file-upload.upload-directory=uploads/
app.file-upload.max-file-size=10485760
app.file-upload.allowed-image-types=image/jpeg,image/png,image/gif,image/webp
```

### گام 3: ایجاد دایرکتری آپلود

```bash
mkdir uploads
mkdir uploads/images
mkdir uploads/products
```

## پیکربندی سریع

### تنظیمات پیش‌فرض (Recommended)

```yaml
app:
  file-upload:
    upload-directory: "uploads/"
    max-file-size: 10485760  # 10MB
    allowed-image-types:
      - "image/jpeg"
      - "image/png" 
      - "image/gif"
      - "image/webp"
    image-processing:
      enable-watermark: true
      watermark:
        text: "Persia Bazaar"
        position: "BOTTOM_RIGHT"
        opacity: 0.7
      thumbnail-sizes:
        - name: "small"
          width: 150
          height: 150
        - name: "medium" 
          width: 600
          height: 600
        - name: "large"
          width: 1200
          height: 1200
```

## استفاده از APIها

### 1. آپلود تصویر ساده

```java
@RestController
@RequestMapping("/api/images")
public class QuickImageController {
    
    @Autowired
    private FileValidationService validationService;
    
    @Autowired
    private LocalFileStorageService storageService;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. اعتبارسنجی
            FileValidationResult validation = validationService.validateFile(file);
            if (!validation.isValid()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", validation.getErrorMessage()));
            }
            
            // 2. ذخیره
            FileStorageResult result = storageService.storeFile(file, "images");
            
            // 3. پاسخ
            return ResponseEntity.ok(Map.of(
                "success", true,
                "fileId", result.getFileReference(),
                "url", "/api/images/" + result.getFileReference()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}
```

### 2. آپلود با پردازش تصویر

```java
@PostMapping("/upload-processed")
public ResponseEntity<?> uploadWithProcessing(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "enableWatermark", defaultValue = "true") boolean enableWatermark) {
    
    try {
        // اعتبارسنجی
        FileValidationResult validation = validationService.validateFile(file);
        if (!validation.isValid()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", validation.getErrorMessage()));
        }
        
        // تنظیمات پردازش
        ImageProcessingOptions options = ImageProcessingOptions.builder()
            .withSizes(Arrays.asList(
                new ImageSize("thumbnail", 150, 150),
                new ImageSize("medium", 600, 600)
            ))
            .enableWatermark(enableWatermark)
            .build();
        
        // پردازش
        ImageProcessingResult result = imageProcessingService.processImage(file, options);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "originalId", result.getOriginalFileId(),
            "variants", result.getVariants()
        ));
        
    } catch (Exception e) {
        return ResponseEntity.status(500)
            .body(Map.of("error", "Processing failed: " + e.getMessage()));
    }
}
```

## نمونه کدهای آماده

### 1. آپلود از فرم HTML

```html
<!DOCTYPE html>
<html>
<head>
    <title>File Upload Test</title>
</head>
<body>
    <form id="uploadForm" enctype="multipart/form-data">
        <input type="file" name="file" accept="image/*" required>
        <button type="submit">Upload</button>
    </form>
    
    <div id="result"></div>
    
    <script>
        document.getElementById('uploadForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData(e.target);
            
            try {
                const response = await fetch('/api/images/upload', {
                    method: 'POST',
                    body: formData
                });
                
                const result = await response.json();
                document.getElementById('result').innerHTML = 
                    `<p>Result: ${JSON.stringify(result, null, 2)}</p>`;
                    
            } catch (error) {
                document.getElementById('result').innerHTML = 
                    `<p>Error: ${error.message}</p>`;
            }
        });
    </script>
</body>
</html>
```

### 2. آپلود با JavaScript/Axios

```javascript
// تابع آپلود ساده
async function uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    try {
        const response = await axios.post('/api/images/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: (progressEvent) => {
                const progress = Math.round(
                    (progressEvent.loaded * 100) / progressEvent.total
                );
                console.log(`Upload progress: ${progress}%`);
            }
        });
        
        return response.data;
    } catch (error) {
        throw new Error(`Upload failed: ${error.response?.data?.error || error.message}`);
    }
}

// استفاده
document.getElementById('fileInput').addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (file) {
        try {
            const result = await uploadImage(file);
            console.log('Upload successful:', result);
        } catch (error) {
            console.error('Upload failed:', error.message);
        }
    }
});
```

### 3. کلاس سرویس برای استفاده در کدهای دیگر

```java
@Service
public class QuickUploadService {
    
    @Autowired
    private FileValidationService validationService;
    
    @Autowired
    private FileStorageService storageService;
    
    @Autowired
    private ImageProcessingService imageProcessingService;
    
    /**
     * آپلود ساده برای استفاده در سایر بخش‌ها
     */
    public String uploadSimple(MultipartFile file) throws Exception {
        // اعتبارسنجی
        FileValidationResult validation = validationService.validateFile(file);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrorMessage());
        }
        
        // ذخیره
        FileStorageResult result = storageService.storeFile(file, "temp");
        return result.getFileReference();
    }
    
    /**
     * آپلود با پردازش کامل
     */
    public Map<String, Object> uploadWithProcessing(MultipartFile file, String category) throws Exception {
        // اعتبارسنجی
        FileValidationResult validation = validationService.validateFile(file);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrorMessage());
        }
        
        // پردازش
        ImageProcessingOptions options = getDefaultProcessingOptions();
        ImageProcessingResult processing = imageProcessingService.processImage(file, options);
        
        // ذخیره
        FileStorageResult storage = storageService.storeFile(file, category);
        
        return Map.of(
            "fileId", storage.getFileReference(),
            "originalSize", file.getSize(),
            "variants", processing.getVariants(),
            "url", "/api/images/" + storage.getFileReference()
        );
    }
    
    private ImageProcessingOptions getDefaultProcessingOptions() {
        return ImageProcessingOptions.builder()
            .withSizes(Arrays.asList(
                new ImageSize("thumbnail", 150, 150),
                new ImageSize("medium", 600, 600)
            ))
            .enableWatermark(true)
            .build();
    }
}
```

### 4. Validation مخصوص

```java
@Component
public class CustomFileValidator {
    
    public boolean isValidProductImage(MultipartFile file) {
        try {
            // بررسی نوع فایل
            if (!file.getContentType().startsWith("image/")) {
                return false;
            }
            
            // بررسی اندازه (حداکثر 5MB برای تصاویر محصول)
            if (file.getSize() > 5 * 1024 * 1024) {
                return false;
            }
            
            // بررسی ابعاد تصویر
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return false;
            }
            
            // حداقل 200x200 پیکسل
            if (image.getWidth() < 200 || image.getHeight() < 200) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}
```

## تست و عیب‌یابی

### 1. تست API با curl

```bash
# تست آپلود ساده
curl -X POST \
  http://localhost:8080/api/images/upload \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/image.jpg'

# تست آپلود با پردازش
curl -X POST \
  http://localhost:8080/api/images/upload-processed \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/image.jpg' \
  -F 'enableWatermark=true'
```

### 2. بررسی لاگ‌ها

```properties
# در application.properties برای دیباگ
logging.level.com.webrayan.bazaar.service=DEBUG
logging.level.org.springframework.web.multipart=DEBUG
```

### 3. مشکلات متداول و راه‌حل

#### مشکل: "Maximum upload size exceeded"
```properties
# افزایش حد آپلود
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

#### مشکل: "Invalid file type"
```java
// بررسی تنظیمات allowed types
app.file-upload.allowed-image-types=image/jpeg,image/png,image/gif,image/webp
```

#### مشکل: دایرکتری آپلود وجود ندارد
```java
@PostConstruct
public void createUploadDirectories() {
    try {
        Files.createDirectories(Paths.get(uploadDirectory));
        Files.createDirectories(Paths.get(uploadDirectory, "images"));
        Files.createDirectories(Paths.get(uploadDirectory, "products"));
    } catch (IOException e) {
        throw new RuntimeException("Cannot create upload directories", e);
    }
}
```

### 4. مونیتورینگ ساده

```java
@RestController
@RequestMapping("/api/system")
public class SystemStatusController {
    
    @GetMapping("/upload-status")
    public Map<String, Object> getUploadStatus() {
        Path uploadDir = Paths.get("uploads");
        
        try {
            long totalFiles = Files.walk(uploadDir)
                .filter(Files::isRegularFile)
                .count();
                
            long totalSize = Files.walk(uploadDir)
                .filter(Files::isRegularFile)
                .mapToLong(this::getFileSize)
                .sum();
                
            return Map.of(
                "status", "healthy",
                "totalFiles", totalFiles,
                "totalSizeMB", totalSize / (1024 * 1024),
                "uploadDirectoryExists", Files.exists(uploadDir)
            );
            
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "error", e.getMessage()
            );
        }
    }
    
    private long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }
}
```

## چک‌لیست راه‌اندازی

- [ ] وابستگی‌ها در pom.xml اضافه شده
- [ ] تنظیمات application.properties انجام شده  
- [ ] دایرکتری uploads ایجاد شده
- [ ] API endpoint اضافه شده
- [ ] تست اولیه با curl یا Postman انجام شده
- [ ] بررسی لاگ‌ها برای خطاهای احتمالی
- [ ] تست آپلود فایل‌های مختلف (jpeg, png, gif)
- [ ] بررسی ذخیره‌سازی فایل‌ها در دایرکتری

این راهنما برای شروع سریع کار با سیستم آپلود فایل کافی است. برای جزئیات بیشتر به Developer Guide مراجعه کنید.
