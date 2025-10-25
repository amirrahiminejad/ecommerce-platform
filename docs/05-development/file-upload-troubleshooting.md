# File Upload System - Troubleshooting Guide

## فهرست مطالب

1. [مشکلات متداول](#مشکلات-متداول)
2. [خطاهای پیکربندی](#خطاهای-پیکربندی)
3. [مشکلات Performance](#مشکلات-performance)
4. [خطاهای Security](#خطاهای-security)
5. [ابزارهای تشخیص](#ابزارهای-تشخیص)
6. [FAQ](#frequently-asked-questions)

## مشکلات متداول

### 1. "Maximum upload size exceeded"

**علامت‌ها:**
```
org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: 
The field file exceeds its maximum permitted size of 1048576 bytes.
```

**راه‌حل:**
```properties
# در application.properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# یا در application.yml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

**تست راه‌حل:**
```bash
curl -X POST http://localhost:8080/api/images/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@large-image.jpg"
```

---

### 2. "Directory not found" or "Access denied"

**علامت‌ها:**
```
java.nio.file.NoSuchFileException: uploads/images
java.nio.file.AccessDeniedException: uploads/images
```

**راه‌حل:**
```java
@PostConstruct
public void initializeDirectories() {
    try {
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // بررسی دسترسی نوشتن
        if (!Files.isWritable(uploadPath)) {
            throw new RuntimeException("Upload directory is not writable: " + uploadPath);
        }
        
    } catch (IOException e) {
        throw new RuntimeException("Failed to initialize upload directories", e);
    }
}
```

**تست راه‌حل:**
```java
@Test
public void testUploadDirectoryAccess() {
    Path uploadDir = Paths.get("uploads");
    assertTrue(Files.exists(uploadDir), "Upload directory should exist");
    assertTrue(Files.isWritable(uploadDir), "Upload directory should be writable");
}
```

---

### 3. "Invalid file type"

**علامت‌ها:**
```json
{
  "error": "File type image/svg+xml is not allowed"
}
```

**راه‌حل:**
```properties
# اضافه کردن نوع فایل جدید
app.file-upload.allowed-image-types=image/jpeg,image/png,image/gif,image/webp,image/svg+xml
```

**یا validation سفارشی:**
```java
@Component
public class CustomFileTypeValidator {
    
    public boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        
        // بررسی magic bytes به جای content type
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            is.read(header);
            
            // JPEG: FF D8 FF
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) {
                return true;
            }
            
            // PNG: 89 50 4E 47
            if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
                return true;
            }
            
            return false;
            
        } catch (IOException e) {
            return false;
        }
    }
}
```

---

### 4. "OutOfMemoryError" در پردازش تصاویر

**علامت‌ها:**
```
java.lang.OutOfMemoryError: Java heap space
at java.awt.image.DataBufferByte.<init>
```

**راه‌حل:**
```java
@Service
public class MemoryEfficientImageProcessor {
    
    public BufferedImage processLargeImage(MultipartFile file) throws IOException {
        // استفاده از streaming بجای load کامل
        try (ImageInputStream iis = ImageIO.createImageInputStream(file.getInputStream())) {
            ImageReader reader = ImageIO.getImageReaders(iis).next();
            reader.setInput(iis);
            
            // تنظیمات بهینه‌سازی حافظه
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceSubsampling(2, 2, 0, 0); // نصف کردن resolution
            
            BufferedImage image = reader.read(0, param);
            reader.dispose();
            
            return image;
        }
    }
}
```

**تنظیمات JVM:**
```bash
# افزایش heap size
java -Xmx2G -Xms512M -jar your-app.jar

# یا در application.properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
```

---

### 5. "Watermark not applied"

**علامت‌ها:**
- تصاویر بدون watermark ذخیره می‌شوند
- هیچ خطایی نمایش داده نمی‌شود

**تشخیص:**
```java
@Test
public void testWatermarkApplication() {
    // تست فایل
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", 
        getClass().getResourceAsStream("/test-image.jpg")
    );
    
    ImageProcessingOptions options = ImageProcessingOptions.builder()
        .enableWatermark(true)
        .build();
    
    ImageProcessingResult result = imageProcessingService.processImage(file, options);
    
    // بررسی اعمال watermark
    assertNotNull(result.getWatermarkInfo());
    assertTrue(result.getWatermarkInfo().isApplied());
}
```

**راه‌حل:**
```java
@Service
public class WatermarkService {
    
    public BufferedImage addWatermark(BufferedImage originalImage, WatermarkOptions options) {
        try {
            Graphics2D g2d = originalImage.createGraphics();
            
            // تنظیمات کیفیت
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // تنظیم شفافیت
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, options.getOpacity()));
            
            // فونت و رنگ
            g2d.setFont(new Font("Arial", Font.BOLD, calculateFontSize(originalImage, options.getText())));
            g2d.setColor(Color.WHITE);
            
            // محاسبه موقعیت
            FontMetrics fm = g2d.getFontMetrics();
            int x = calculateXPosition(originalImage.getWidth(), fm.stringWidth(options.getText()), options.getPosition());
            int y = calculateYPosition(originalImage.getHeight(), fm.getHeight(), options.getPosition());
            
            // رسم watermark
            g2d.drawString(options.getText(), x, y);
            g2d.dispose();
            
            return originalImage;
            
        } catch (Exception e) {
            logger.error("Failed to apply watermark", e);
            return originalImage; // برگرداندن تصویر اصلی در صورت خطا
        }
    }
}
```

## خطاهای پیکربندی

### 1. Bean not found errors

**علامت‌ها:**
```
No qualifying bean of type 'FileValidationService' available
```

**راه‌حل:**
```java
// اطمینان از اسکن component
@SpringBootApplication
@ComponentScan(basePackages = "com.webrayan.bazaar")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// یا تعریف دستی bean
@Configuration
public class FileUploadConfig {
    
    @Bean
    public FileValidationService fileValidationService() {
        return new FileValidationService();
    }
}
```

### 2. Configuration properties not loaded

**علامت‌ها:**
```
Binding to target org.springframework.boot.context.properties.bind.BindException failed
```

**راه‌حل:**
```java
@ConfigurationProperties(prefix = "app.file-upload")
@Component // این annotation ضروری است
@Validated
public class FileUploadConfig {
    
    @NotNull
    private String uploadDirectory;
    
    @Min(1024)
    private Long maxFileSize;
    
    // getters and setters
}
```

## مشکلات Performance

### 1. آپلود کند

**تشخیص:**
```java
@Component
public class PerformanceMonitor {
    
    @EventListener
    public void onUploadStart(UploadStartEvent event) {
        event.setStartTime(System.currentTimeMillis());
    }
    
    @EventListener
    public void onUploadComplete(UploadCompleteEvent event) {
        long duration = System.currentTimeMillis() - event.getStartTime();
        if (duration > 30000) { // بیش از 30 ثانیه
            logger.warn("Slow upload detected: {} ms for file: {}", 
                duration, event.getFilename());
        }
    }
}
```

**راه‌حل:**
```java
// پردازش async
@Async("fileProcessingExecutor")
public CompletableFuture<FileProcessingResult> processFileAsync(MultipartFile file) {
    return CompletableFuture.supplyAsync(() -> {
        return processFile(file);
    });
}

// Thread pool optimization
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("fileProcessingExecutor")
    public TaskExecutor fileProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("file-proc-");
        executor.initialize();
        return executor;
    }
}
```

### 2. استفاده بالای حافظه

**تشخیص:**
```java
@RestController
public class MemoryMonitorController {
    
    @GetMapping("/api/system/memory")
    public Map<String, Object> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        
        return Map.of(
            "totalMemoryMB", runtime.totalMemory() / (1024 * 1024),
            "freeMemoryMB", runtime.freeMemory() / (1024 * 1024),
            "maxMemoryMB", runtime.maxMemory() / (1024 * 1024),
            "usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        );
    }
}
```

**راه‌حل:**
```java
// Stream processing
public void processLargeFile(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream();
         BufferedInputStream bufferedStream = new BufferedInputStream(inputStream)) {
        
        byte[] buffer = new byte[8192]; // 8KB buffer
        int bytesRead;
        
        while ((bytesRead = bufferedStream.read(buffer)) != -1) {
            processChunk(buffer, bytesRead);
        }
        
    } catch (IOException e) {
        throw new ProcessingException("Failed to process file", e);
    }
}
```

## خطاهای Security

### 1. Directory traversal attack

**تشخیص:**
```java
@Test
public void testDirectoryTraversalPrevention() {
    String maliciousFilename = "../../../etc/passwd";
    
    assertThrows(SecurityException.class, () -> {
        fileValidationService.validateFilename(maliciousFilename);
    });
}
```

**راه‌حل:**
```java
public String sanitizeFilename(String filename) {
    if (filename == null || filename.trim().isEmpty()) {
        return "unnamed_" + System.currentTimeMillis();
    }
    
    // حذف path separators
    filename = filename.replaceAll("[/\\\\]", "_");
    
    // حذف کاراکترهای خطرناک
    filename = filename.replaceAll("[<>:\"|?*]", "_");
    
    // حذف .. و .
    filename = filename.replaceAll("\\.+", ".");
    
    // حداکثر طول
    if (filename.length() > 255) {
        String extension = getFileExtension(filename);
        filename = filename.substring(0, 250 - extension.length()) + extension;
    }
    
    return filename;
}
```

### 2. Malicious file upload

**تشخیص:**
```java
public boolean containsMaliciousContent(byte[] fileContent) {
    String content = new String(fileContent, StandardCharsets.UTF_8);
    
    // الگوهای مشکوک
    String[] maliciousPatterns = {
        "<script", "javascript:", "vbscript:", 
        "onload=", "onerror=", "<?php", "<%"
    };
    
    String lowerContent = content.toLowerCase();
    for (String pattern : maliciousPatterns) {
        if (lowerContent.contains(pattern)) {
            return true;
        }
    }
    
    return false;
}
```

## ابزارهای تشخیص

### 1. Health Check Endpoint

```java
@Component
public class FileUploadHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // بررسی دایرکتری آپلود
            Path uploadDir = Paths.get(uploadDirectory);
            boolean dirExists = Files.exists(uploadDir);
            boolean dirWritable = Files.isWritable(uploadDir);
            
            // بررسی فضای ذخیره‌سازی
            long freeSpace = uploadDir.toFile().getFreeSpace();
            long totalSpace = uploadDir.toFile().getTotalSpace();
            double usagePercent = ((double) (totalSpace - freeSpace) / totalSpace) * 100;
            
            if (!dirExists || !dirWritable || usagePercent > 90) {
                return Health.down()
                    .withDetail("uploadDirectory", dirExists ? "exists" : "missing")
                    .withDetail("writable", dirWritable)
                    .withDetail("diskUsagePercent", usagePercent)
                    .build();
            }
            
            return Health.up()
                .withDetail("uploadDirectory", "healthy")
                .withDetail("diskUsagePercent", usagePercent)
                .build();
                
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

### 2. Debug Logging

```properties
# application-debug.properties
logging.level.com.webrayan.bazaar.service.FileValidationService=DEBUG
logging.level.com.webrayan.bazaar.service.ImageProcessingService=DEBUG
logging.level.org.springframework.web.multipart=DEBUG
logging.level.org.apache.tomcat.util.http.fileupload=DEBUG

# فایل لاگ جداگانه برای آپلودها
logging.file.name=logs/file-upload.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 3. Metrics Collection

```java
@Component
public class FileUploadMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter uploadAttempts;
    private final Counter uploadSuccesses;
    private final Counter uploadFailures;
    private final Timer processingTime;
    
    public FileUploadMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.uploadAttempts = Counter.builder("file.upload.attempts").register(meterRegistry);
        this.uploadSuccesses = Counter.builder("file.upload.successes").register(meterRegistry);
        this.uploadFailures = Counter.builder("file.upload.failures").register(meterRegistry);
        this.processingTime = Timer.builder("file.processing.time").register(meterRegistry);
    }
    
    public void recordUploadAttempt() {
        uploadAttempts.increment();
    }
    
    public void recordUploadSuccess(String fileType, long fileSize) {
        uploadSuccesses.increment(
            Tags.of("type", fileType, "size_range", getSizeRange(fileSize))
        );
    }
}
```

## Frequently Asked Questions

### Q: چگونه حجم فایل‌های قابل آپلود را افزایش دهم؟

**A:** در سه سطح تنظیم کنید:

```properties
# Spring Boot
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# Application
app.file-upload.max-file-size=52428800

# Tomcat (اگر نیاز باشد)
server.tomcat.max-http-post-size=52428800
```

### Q: چگونه فایل‌ها را در cloud storage ذخیره کنم؟

**A:** پیاده‌سازی CloudStorageService:

```java
@Service
@ConditionalOnProperty(value = "app.storage.type", havingValue = "cloud")
public class S3StorageService implements FileStorageService {
    
    @Autowired
    private AmazonS3 s3Client;
    
    @Value("${app.cloud.s3.bucket}")
    private String bucketName;
    
    @Override
    public FileStorageResult storeFile(MultipartFile file, String directory) {
        try {
            String key = directory + "/" + generateUniqueFilename(file.getOriginalFilename());
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            
            s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
            
            return FileStorageResult.success(key, generateUrl(key));
            
        } catch (Exception e) {
            throw new StorageException("Failed to store file in S3", e);
        }
    }
}
```

### Q: چگونه پردازش تصاویر را بهینه کنم؟

**A:** از async processing و caching استفاده کنید:

```java
@Service
public class OptimizedImageService {
    
    @Async
    @Cacheable(value = "processedImages", key = "#file.originalFilename + '_' + #options.hashCode()")
    public CompletableFuture<ImageProcessingResult> processImageAsync(
            MultipartFile file, ImageProcessingOptions options) {
        
        // پردازش در background
        ImageProcessingResult result = imageProcessingService.processImage(file, options);
        return CompletableFuture.completedFuture(result);
    }
}
```

### Q: چگونه از duplicate file uploads جلوگیری کنم؟

**A:** با checksum comparison:

```java
@Service
public class DuplicateDetectionService {
    
    public String calculateFileHash(MultipartFile file) throws IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        
        return bytesToHex(md.digest());
    }
    
    public boolean isDuplicate(String fileHash) {
        return fileRepository.existsByFileHash(fileHash);
    }
}
```

این راهنما شامل تمام مشکلات متداول و راه‌حل‌های آن‌ها برای سیستم آپلود فایل است.
