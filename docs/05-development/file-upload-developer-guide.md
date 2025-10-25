# Developer Guide: File Upload System Implementation

## Overview

This guide provides technical documentation for developers working with the Persia Bazaar file upload system. It covers architecture, implementation patterns, extension points, and best practices for maintaining and extending the system.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Core Components](#core-components)
3. [Implementation Patterns](#implementation-patterns)
4. [Extension Points](#extension-points)
5. [Configuration Management](#configuration-management)
6. [Testing Strategies](#testing-strategies)
7. [Performance Optimization](#performance-optimization)
8. [Security Considerations](#security-considerations)
9. [Troubleshooting](#troubleshooting)
10. [Best Practices](#best-practices)

## Architecture Overview

### System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                          Client Layer                           │
├─────────────────────────────────────────────────────────────────┤
│  Web Browser  │  Mobile App  │  API Client  │  Postman/Testing │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Controller Layer                          │
├─────────────────────────────────────────────────────────────────┤
│  ImageController  │  ProductImageController  │  FileController  │
│  - Upload APIs    │  - Gallery Management   │  - Download APIs │
│  - Validation     │  - Primary Selection    │  - Metadata APIs │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Service Layer                            │
├─────────────────────────────────────────────────────────────────┤
│  FileValidationService  │  ImageProcessingService  │  Storage   │
│  - Security Checks      │  - Resizing              │  Service   │
│  - Type Validation      │  - Format Conversion     │  - Local   │
│  - Size Limits          │  - Watermarking          │  - Cloud   │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Repository Layer                           │
├─────────────────────────────────────────────────────────────────┤
│  ImageRepository  │  ProductImageRepository  │  VariantRepo     │
│  - Metadata CRUD  │  - Gallery Management   │  - Size Variants │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Storage Layer                             │
├─────────────────────────────────────────────────────────────────┤
│    Local Storage    │    Cloud Storage    │    Database        │
│    - File System    │    - AWS S3        │    - Metadata      │
│    - Direct Access  │    - Google Cloud  │    - Relations     │
└─────────────────────────────────────────────────────────────────┘
```

### Data Flow

```
1. File Upload Request
   ↓
2. Controller Validation (Basic)
   ↓
3. Service Layer Processing
   ├── FileValidationService (Security, Type, Size)
   ├── ImageProcessingService (Resize, Convert, Watermark)
   └── FileStorageService (Store, Organize)
   ↓
4. Database Operations
   ├── Save Metadata
   ├── Create Variants
   └── Update Relations
   ↓
5. Response Generation
```

## Core Components

### 1. FileValidationService

**Purpose**: Comprehensive file validation and security checks

```java
@Service
public class FileValidationService {
    
    /**
     * Main validation method - entry point for all file validation
     */
    public FileValidationResult validateFile(MultipartFile file) {
        // Implementation details in previous sections
    }
    
    /**
     * Magic byte validation - prevents file type spoofing
     */
    private boolean isValidImageFile(MultipartFile file) throws IOException {
        // Reads file header and validates against known image signatures
    }
    
    /**
     * Security pattern detection
     */
    private boolean containsMaliciousPatterns(String filename) {
        // Scans for directory traversal, script injection, etc.
    }
}
```

**Key Methods for Extension**:
- `validateFile()`: Main validation entry point
- `validateFiles()`: Batch validation
- `generateSecureFilename()`: Filename sanitization
- Custom validation rules can be added by overriding validation methods

### 2. ImageProcessingService

**Purpose**: Advanced image manipulation and optimization

```java
@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    
    /**
     * Main processing pipeline
     */
    public ImageProcessingResult processImage(MultipartFile file, 
                                            ImageProcessingOptions options) {
        // 1. Extract metadata
        // 2. Generate size variants
        // 3. Apply watermarks
        // 4. Optimize quality
        // 5. Return processed variants
    }
    
    /**
     * Resize with different modes
     */
    public byte[] resizeImage(byte[] imageData, int width, int height, 
                             ResizeMode mode) {
        // PROPORTIONAL, EXACT, CROP, FIT modes
    }
}
```

**Extension Points**:
- Custom resize algorithms
- Additional watermark types
- New image formats
- Custom optimization strategies

### 3. FileStorageService

**Purpose**: Abstract storage operations for multiple backends

```java
public interface FileStorageService {
    FileStorageResult storeFile(MultipartFile file, String directory);
    byte[] retrieveFile(String fileReference);
    boolean deleteFile(String fileReference);
    // ... other operations
}
```

**Implementation Strategy**:
```java
@Service
@Primary  // Default implementation
public class LocalFileStorageService implements FileStorageService {
    // Local file system implementation
}

@Service
@Conditional(CloudStorageCondition.class)
public class CloudFileStorageService implements FileStorageService {
    // Cloud storage implementation (S3, GCS, etc.)
}
```

## Implementation Patterns

### 1. Strategy Pattern for Storage Backends

```java
@Component
public class StorageServiceFactory {
    
    @Autowired
    private List<FileStorageService> storageServices;
    
    public FileStorageService getStorageService(String type) {
        return storageServices.stream()
            .filter(service -> service.supports(type))
            .findFirst()
            .orElse(defaultStorageService);
    }
}
```

### 2. Builder Pattern for Processing Options

```java
public class ImageProcessingOptions {
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ImageProcessingOptions options = new ImageProcessingOptions();
        
        public Builder withSizes(List<ImageSize> sizes) {
            options.setSizes(sizes);
            return this;
        }
        
        public Builder enableWatermark(WatermarkOptions watermark) {
            options.setEnableWatermark(true);
            options.setWatermarkOptions(watermark);
            return this;
        }
        
        public ImageProcessingOptions build() {
            return options;
        }
    }
}

// Usage
ImageProcessingOptions options = ImageProcessingOptions.builder()
    .withSizes(Arrays.asList(
        new ImageSize("thumbnail", 150, 150),
        new ImageSize("medium", 600, 600)
    ))
    .enableWatermark(watermarkOptions)
    .build();
```

### 3. Template Method Pattern for Upload Processing

```java
@Service
public abstract class AbstractUploadProcessor {
    
    public final UploadResult processUpload(MultipartFile file, UploadContext context) {
        // Template method defining the upload process
        validateInput(file, context);
        FileValidationResult validation = validateFile(file);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrorMessage());
        }
        
        ProcessingResult processing = processFile(file, context);
        StorageResult storage = storeFile(processing, context);
        MetadataResult metadata = saveMetadata(storage, context);
        
        return buildResult(processing, storage, metadata);
    }
    
    // Abstract methods for specific implementations
    protected abstract ProcessingResult processFile(MultipartFile file, UploadContext context);
    protected abstract StorageResult storeFile(ProcessingResult processing, UploadContext context);
    protected abstract MetadataResult saveMetadata(StorageResult storage, UploadContext context);
}
```

## Extension Points

### 1. Adding Custom Validation Rules

```java
@Component
public class CustomFileValidator implements FileValidator {
    
    @Override
    public ValidationResult validate(MultipartFile file, ValidationContext context) {
        // Custom validation logic
        if (isVirusDetected(file)) {
            return ValidationResult.invalid("Malware detected");
        }
        
        if (isDuplicateContent(file)) {
            return ValidationResult.warning("Duplicate content detected");
        }
        
        return ValidationResult.valid();
    }
    
    @Override
    public int getOrder() {
        return 100; // Execution order
    }
}

// Register in validation chain
@Configuration
public class ValidationConfig {
    
    @Bean
    public ValidationChain validationChain(List<FileValidator> validators) {
        return new ValidationChain(validators);
    }
}
```

### 2. Custom Storage Backend Implementation

```java
@Service
@ConditionalOnProperty(value = "app.storage.type", havingValue = "custom")
public class CustomStorageService implements FileStorageService {
    
    @Override
    public FileStorageResult storeFile(MultipartFile file, String directory) {
        // Custom storage implementation
        // Could be database BLOB, custom API, etc.
    }
    
    @Override
    public boolean supports(String storageType) {
        return "custom".equals(storageType);
    }
}
```

### 3. Custom Image Processing Effects

```java
@Component
public class CustomImageProcessor implements ImageProcessor {
    
    @Override
    public byte[] process(byte[] imageData, ProcessingOptions options) {
        // Custom image processing
        // Could be filters, AI enhancement, etc.
        return enhancedImageData;
    }
    
    @Override
    public boolean supports(String effectType) {
        return "ai-enhance".equals(effectType);
    }
}
```

## Configuration Management

### 1. Environment-Specific Configuration

```yaml
# application-dev.yml
app:
  file-upload:
    max-file-size: 5MB
    enable-watermark: false
    storage-type: local
    upload-directory: "dev-uploads/"

---
# application-prod.yml
app:
  file-upload:
    max-file-size: 10MB
    enable-watermark: true
    storage-type: cloud
    cloud-config:
      provider: aws-s3
      bucket: prod-iran-bazaar-uploads
      region: us-west-2
```

### 2. Dynamic Configuration Updates

```java
@Component
@RefreshScope
public class DynamicFileUploadConfig {
    
    @Value("${app.file-upload.max-file-size:10485760}")
    private Long maxFileSize;
    
    @EventListener
    public void handleConfigUpdate(ConfigUpdateEvent event) {
        // React to configuration changes
        if (event.getKey().startsWith("app.file-upload")) {
            refreshConfiguration();
        }
    }
}
```

### 3. Feature Flags

```java
@Component
public class FeatureFlags {
    
    @Value("${features.advanced-image-processing:false}")
    private boolean advancedImageProcessing;
    
    @Value("${features.ai-content-detection:false}")
    private boolean aiContentDetection;
    
    public boolean isAdvancedImageProcessingEnabled() {
        return advancedImageProcessing;
    }
}
```

## Testing Strategies

### 1. Unit Testing with Mocks

```java
@ExtendWith(MockitoExtension.class)
class FileValidationServiceTest {
    
    @Mock
    private FileUploadConfig config;
    
    @InjectMocks
    private FileValidationService validationService;
    
    @Test
    void testValidJpegFile() {
        // Mock configuration
        when(config.getAllowedImageTypes())
            .thenReturn(Arrays.asList("image/jpeg"));
        
        // Create test file
        MockMultipartFile file = createMockJpegFile();
        
        // Test validation
        FileValidationResult result = validationService.validateFile(file);
        
        assertTrue(result.isValid());
    }
    
    private MockMultipartFile createMockJpegFile() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        return new MockMultipartFile("file", "test.jpg", "image/jpeg", jpegHeader);
    }
}
```

### 2. Integration Testing

```java
@SpringBootTest
@TestPropertySource(properties = {
    "app.file-upload.upload-directory=test-uploads/",
    "app.file-upload.max-file-size=1048576"
})
class FileUploadIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @TempDir
    Path tempDir;
    
    @Test
    void testCompleteUploadWorkflow() {
        // Prepare test file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(createTestImage()));
        
        // Upload request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/images/upload", 
            new HttpEntity<>(body, headers), 
            String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

### 3. Performance Testing

```java
@Test
void testBatchUploadPerformance() {
    List<MultipartFile> files = createTestFiles(10);
    
    long startTime = System.currentTimeMillis();
    
    List<FileStorageResult> results = fileStorageService.storeFiles(files, "performance-test");
    
    long endTime = System.currentTimeMillis();
    long processingTime = endTime - startTime;
    
    assertEquals(10, results.size());
    assertTrue(processingTime < 30000); // Should complete within 30 seconds
}
```

## Performance Optimization

### 1. Asynchronous Processing

```java
@Service
public class AsyncImageProcessingService {
    
    @Async("imageProcessingExecutor")
    public CompletableFuture<ImageProcessingResult> processImageAsync(
            MultipartFile file, ImageProcessingOptions options) {
        
        try {
            ImageProcessingResult result = processImage(file, options);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("imageProcessingExecutor")
    public TaskExecutor imageProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("image-processing-");
        executor.initialize();
        return executor;
    }
}
```

### 2. Caching Strategy

```java
@Service
public class CachedImageService {
    
    @Cacheable(value = "processedImages", key = "#file.originalFilename + '_' + #options.hashCode()")
    public ImageProcessingResult processImage(MultipartFile file, ImageProcessingOptions options) {
        // Expensive image processing
        return imageProcessingService.processImage(file, options);
    }
    
    @CacheEvict(value = "processedImages", key = "#filename + '*'", allEntries = true)
    public void evictImageCache(String filename) {
        // Manual cache eviction
    }
}
```

### 3. Memory Management

```java
@Component
public class MemoryOptimizedProcessor {
    
    public void processLargeImage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Stream processing instead of loading entire file into memory
            BufferedImage image = ImageIO.read(inputStream);
            
            // Process in chunks
            processImageInChunks(image);
            
        } catch (IOException e) {
            throw new ProcessingException("Failed to process large image", e);
        }
    }
    
    private void processImageInChunks(BufferedImage image) {
        int chunkSize = 1000; // Process 1000x1000 pixel chunks
        
        for (int y = 0; y < image.getHeight(); y += chunkSize) {
            for (int x = 0; x < image.getWidth(); x += chunkSize) {
                BufferedImage chunk = image.getSubimage(
                    x, y,
                    Math.min(chunkSize, image.getWidth() - x),
                    Math.min(chunkSize, image.getHeight() - y)
                );
                
                processChunk(chunk);
            }
        }
    }
}
```

## Security Considerations

### 1. Input Sanitization

```java
@Component
public class SecurityService {
    
    private static final Pattern SAFE_FILENAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9._-]+$");
    
    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed_" + System.currentTimeMillis();
        }
        
        // Remove path separators
        filename = filename.replaceAll("[/\\\\]", "_");
        
        // Remove dangerous characters
        filename = filename.replaceAll("[<>:\"|?*]", "_");
        
        // Ensure safe pattern
        if (!SAFE_FILENAME_PATTERN.matcher(filename).matches()) {
            filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        }
        
        return filename;
    }
    
    public boolean isPathTraversalAttempt(String path) {
        return path.contains("..") || 
               path.contains("./") || 
               path.contains("\\");
    }
}
```

### 2. Rate Limiting

```java
@Component
public class UploadRateLimiter {
    
    private final LoadingCache<String, AtomicInteger> uploadCounts;
    
    public UploadRateLimiter() {
        this.uploadCounts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(key -> new AtomicInteger(0));
    }
    
    public boolean isAllowed(String userIdentifier) {
        AtomicInteger count = uploadCounts.get(userIdentifier);
        return count.incrementAndGet() <= MAX_UPLOADS_PER_HOUR;
    }
}
```

### 3. Content Validation

```java
@Component
public class ContentSecurityService {
    
    public boolean isSafeContent(byte[] fileContent) {
        // Check for embedded scripts
        String content = new String(fileContent, StandardCharsets.UTF_8);
        
        if (containsScript(content)) {
            return false;
        }
        
        // Check for malicious patterns in binary data
        if (containsMaliciousSignatures(fileContent)) {
            return false;
        }
        
        return true;
    }
    
    private boolean containsScript(String content) {
        String[] scriptPatterns = {
            "<script", "javascript:", "vbscript:", "onload=", "onerror="
        };
        
        String lowerContent = content.toLowerCase();
        return Arrays.stream(scriptPatterns)
            .anyMatch(lowerContent::contains);
    }
}
```

## Troubleshooting

### 1. Common Issues and Solutions

#### Issue: OutOfMemoryError during image processing
```java
// Solution: Stream processing and memory management
@Value("${app.image-processing.max-memory-mb:512}")
private int maxMemoryMb;

public void processLargeImage(MultipartFile file) {
    if (file.getSize() > maxMemoryMb * 1024 * 1024) {
        // Use streaming approach
        processImageStream(file);
    } else {
        // Use standard approach
        processImageInMemory(file);
    }
}
```

#### Issue: Slow upload performance
```java
// Solution: Parallel processing
@Async
public CompletableFuture<List<FileStorageResult>> processBatchAsync(
        List<MultipartFile> files) {
    
    return files.parallelStream()
        .map(this::processFile)
        .collect(Collectors.toList())
        .thenApply(CompletableFuture::completedFuture)
        .join();
}
```

### 2. Debugging Tools

```java
@Component
public class UploadDebugService {
    
    @EventListener
    public void handleUploadEvent(UploadProgressEvent event) {
        logger.debug("Upload progress: {}% for file: {}", 
            event.getProgress(), event.getFilename());
    }
    
    public UploadDiagnostics getDiagnostics() {
        return UploadDiagnostics.builder()
            .totalUploads(uploadMetrics.getTotalUploads())
            .successRate(uploadMetrics.getSuccessRate())
            .averageProcessingTime(uploadMetrics.getAverageProcessingTime())
            .storageUsage(storageService.getUsageStatistics())
            .build();
    }
}
```

### 3. Health Checks

```java
@Component
public class FileUploadHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // Check storage availability
            boolean storageHealthy = storageService.healthCheck();
            
            // Check processing capacity
            boolean processingHealthy = imageProcessingService.healthCheck();
            
            if (storageHealthy && processingHealthy) {
                return Health.up()
                    .withDetail("storage", "healthy")
                    .withDetail("processing", "healthy")
                    .build();
            } else {
                return Health.down()
                    .withDetail("storage", storageHealthy ? "healthy" : "unhealthy")
                    .withDetail("processing", processingHealthy ? "healthy" : "unhealthy")
                    .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

## Best Practices

### 1. Code Organization

```
src/main/java/com/webrayan/bazaar/
├── core/
│   ├── config/          # Configuration classes
│   ├── service/         # Core business services
│   ├── validation/      # Validation logic
│   └── util/           # Utility classes
├── modules/
│   └── catalog/
│       ├── controller/  # REST endpoints
│       ├── service/     # Module-specific services
│       ├── repository/  # Data access
│       └── entity/      # JPA entities
└── common/             # Shared components
```

### 2. Error Handling Strategy

```java
@ControllerAdvice
public class FileUploadExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build());
    }
    
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorage(StorageException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .code("STORAGE_ERROR")
                .message("File storage operation failed")
                .timestamp(Instant.now())
                .build());
    }
}
```

### 3. Monitoring and Metrics

```java
@Component
public class UploadMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter uploadCounter;
    private final Timer processingTimer;
    
    public UploadMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.uploadCounter = Counter.builder("file.uploads")
            .description("Total file uploads")
            .register(meterRegistry);
        this.processingTimer = Timer.builder("file.processing.time")
            .description("File processing time")
            .register(meterRegistry);
    }
    
    public void recordUpload(String fileType, long fileSize) {
        uploadCounter.increment(
            Tags.of("type", fileType, "size_range", getSizeRange(fileSize))
        );
    }
    
    public Timer.Sample startProcessingTimer() {
        return Timer.start(meterRegistry);
    }
}
```

### 4. Documentation Standards

```java
/**
 * Processes uploaded images with comprehensive validation and optimization.
 * 
 * <p>This service handles the complete image processing pipeline including:
 * <ul>
 *   <li>Security validation and malware detection</li>
 *   <li>Format conversion and optimization</li>
 *   <li>Multiple size variant generation</li>
 *   <li>Watermark application</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * ImageProcessingOptions options = ImageProcessingOptions.builder()
 *     .withSizes(standardSizes)
 *     .enableWatermark(watermarkConfig)
 *     .build();
 * 
 * ImageProcessingResult result = service.processImage(file, options);
 * }</pre>
 * 
 * @author Developer Team
 * @since 1.0.0
 * @see ImageProcessingOptions
 * @see FileValidationService
 */
@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    // Implementation
}
```

This developer guide provides comprehensive technical documentation for maintaining and extending the file upload system. It covers all aspects from architecture to implementation details that developers need to work effectively with the system.
