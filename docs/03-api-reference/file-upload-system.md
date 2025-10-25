# File Upload Management System

## Overview

The Persia Bazaar file upload management system provides comprehensive functionality for handling image uploads, processing, storage, and management. This system includes advanced features such as automatic image resizing, format conversion, watermarking, security validation, and multi-backend storage support.

## Features

### 🚀 Core Features
- **Secure File Upload**: Advanced validation and security checks
- **Image Processing**: Automatic resizing, format conversion, and optimization
- **Multiple Storage Backends**: Local and cloud storage support
- **Watermarking**: Automatic watermark application
- **Gallery Management**: Product image galleries with primary image selection
- **Format Conversion**: WebP conversion for better compression
- **Security**: File type validation, size limits, and malicious content detection

### 🔧 Technical Features
- **Configurable Settings**: Extensive configuration options
- **RESTful APIs**: Complete REST API with OpenAPI documentation
- **Batch Processing**: Multiple file upload support
- **Metadata Management**: Comprehensive file metadata tracking
- **Error Handling**: Robust error handling and validation
- **Performance Optimization**: Efficient image processing and storage

## System Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Controllers   │    │    Services      │    │   Repositories  │
├─────────────────┤    ├──────────────────┤    ├─────────────────┤
│ ImageController │───▶│ ImageService     │───▶│ ImageRepository │
│                 │    │ FileStorageServ. │    │                 │
│                 │    │ ImageProcessing  │    │                 │
│                 │    │ ValidationServ.  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   File System   │    │   Configuration  │    │    Database     │
│   - Local       │    │   - Validation   │    │   - Metadata    │
│   - Cloud       │    │   - Processing   │    │   - Variants    │
│                 │    │   - Storage      │    │   - Relations   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## Configuration

### Application Properties

```yaml
app:
  file-upload:
    max-file-size: 10485760  # 10MB
    max-files-per-request: 5
    upload-directory: "uploads/"
    temp-directory: "temp/"
    enable-virus-scanning: false
    enable-watermark: true
    watermark-text: "Persia Bazaar"
    jpeg-quality: 85
    enable-webp-conversion: true
    
    allowed-image-types:
      - "image/jpeg"
      - "image/jpg"
      - "image/png"
      - "image/gif"
      - "image/webp"
    
    allowed-image-extensions:
      - ".jpg"
      - ".jpeg"
      - ".png"
      - ".gif"
      - ".webp"
    
    image-sizes:
      - name: "thumbnail"
        width: 150
        height: 150
      - name: "small"
        width: 300
        height: 300
      - name: "medium"
        width: 600
        height: 600
      - name: "large"
        width: 1200
        height: 1200
```

## API Endpoints

### Image Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/images/upload` | Upload single/multiple images |
| GET | `/api/images/view` | View image by size and filename |
| DELETE | `/api/images/delete` | Delete image and variants |
| GET | `/api/images/ad/{adId}` | Get all images for advertisement |

### Product Image APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products/{id}/images` | Upload product images |
| PUT | `/api/products/{id}/images/primary` | Set primary image |
| PUT | `/api/products/{id}/images/reorder` | Reorder images |
| GET | `/api/products/{id}/images` | Get product gallery |
| DELETE | `/api/products/{id}/images/{imageId}` | Delete product image |

## Usage Examples

### 1. Basic Image Upload

```bash
curl -X POST "http://localhost:8080/api/images/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@product-image.jpg" \
  -F "adId=123"
```

### 2. Upload Multiple Product Images

```bash
curl -X POST "http://localhost:8080/api/products/456/images" \
  -H "Content-Type: multipart/form-data" \
  -F "files=@image1.jpg" \
  -F "files=@image2.jpg" \
  -F "files=@image3.jpg"
```

### 3. View Image by Size

```bash
curl "http://localhost:8080/api/images/view?size=medium&filename=product-image.jpg" \
  --output medium-image.jpg
```

### 4. Set Primary Product Image

```bash
curl -X PUT "http://localhost:8080/api/products/456/images/primary" \
  -H "Content-Type: application/json" \
  -d '{"imageId": 789}'
```

## Image Processing Pipeline

### 1. Upload Process

```
File Upload → Validation → Security Check → Storage → Processing → Variants Generation
     ↓             ↓             ↓            ↓           ↓             ↓
  Multipart    File Type     Magic Bytes   Secure     Original    Thumbnails
   Request     Check         Check         Storage    Metadata    Resized
               Size Limit    Malware       Unique                 Optimized
               Extension     Scan          Filename               Watermarked
```

### 2. Validation Steps

1. **File Existence Check**: Ensure file is not null or empty
2. **Size Validation**: Check against maximum file size limit
3. **Type Validation**: Verify MIME type and file extension
4. **Security Check**: Scan for malicious patterns in filename
5. **Header Validation**: Verify file magic bytes match the declared type
6. **Content Scan**: Optional virus scanning for uploaded files

### 3. Processing Steps

1. **Metadata Extraction**: Extract image dimensions, format, and properties
2. **Resize Generation**: Create multiple size variants
3. **Format Optimization**: Convert to optimal formats (WebP when possible)
4. **Quality Optimization**: Apply compression settings
5. **Watermark Application**: Add watermarks if enabled
6. **Storage**: Store original and variants in configured backend

## Security Features

### File Validation
- **Magic Byte Verification**: Validates file headers to prevent spoofing
- **Filename Sanitization**: Removes dangerous characters and patterns
- **Path Traversal Prevention**: Blocks directory traversal attempts
- **Size Limits**: Enforces maximum file and request sizes
- **Type Restrictions**: Only allows whitelisted file types

### Storage Security
- **Unique Filenames**: Generates UUID-based filenames to prevent conflicts
- **Directory Isolation**: Prevents access outside upload directories
- **Access Control**: Secure file serving with proper permissions
- **Temp File Cleanup**: Automatic cleanup of temporary files

## Error Handling

The system provides comprehensive error handling with detailed messages:

### Common Error Codes

| Code | Description | Solution |
|------|-------------|----------|
| 400 | Invalid file type | Use supported image formats (JPEG, PNG, GIF, WebP) |
| 413 | File too large | Reduce file size or increase limit |
| 422 | Validation failed | Check file integrity and format |
| 500 | Processing error | Check logs for processing issues |

### Error Response Format

```json
{
  "error": "File validation failed",
  "message": "File size exceeds maximum limit of 10MB",
  "timestamp": "2025-09-13T10:30:00",
  "path": "/api/images/upload"
}
```

## Performance Optimization

### Image Optimization
- **Progressive JPEG**: Enables progressive loading
- **WebP Conversion**: Automatic conversion for better compression
- **Quality Control**: Configurable compression levels
- **Efficient Resizing**: Uses high-performance image processing libraries

### Storage Optimization
- **Batch Operations**: Supports multiple file processing
- **Lazy Loading**: Deferred loading of image variants
- **Caching**: Metadata caching for improved performance
- **Cleanup Jobs**: Automatic cleanup of unused files

## Monitoring and Logging

### Key Metrics
- Upload success/failure rates
- Processing times
- Storage usage
- Error frequencies
- File type distributions

### Log Levels
- **INFO**: Successful operations and key events
- **WARN**: Validation failures and recoverable errors
- **ERROR**: Processing failures and system errors
- **DEBUG**: Detailed processing information

## Integration Guide

### Adding Custom Storage Backend

1. Implement `FileStorageService` interface
2. Configure storage-specific properties
3. Register implementation as Spring bean
4. Update configuration to use new backend

### Custom Image Processing

1. Extend `ImageProcessingService` interface
2. Add custom processing logic
3. Configure processing options
4. Register custom processor

### Validation Extensions

1. Implement custom validation rules
2. Extend `FileValidationService`
3. Add validation to processing pipeline
4. Configure validation parameters

## Troubleshooting

### Common Issues

1. **Upload Failures**
   - Check file size limits
   - Verify file type support
   - Ensure storage permissions

2. **Processing Errors**
   - Verify image file integrity
   - Check processing configuration
   - Review error logs

3. **Storage Issues**
   - Confirm directory permissions
   - Check available disk space
   - Verify configuration paths

### Debug Tools

- **Health Endpoints**: Monitor system health
- **Metrics Dashboard**: Track performance metrics
- **Log Analysis**: Detailed error investigation
- **Configuration Validation**: Verify setup

## Best Practices

### File Upload
1. Always validate files on both client and server
2. Use secure filename generation
3. Implement proper error handling
4. Monitor upload metrics

### Image Processing
1. Configure appropriate quality settings
2. Use batch processing for multiple files
3. Implement fallback strategies
4. Cache processed variants

### Security
1. Regular security audits
2. Keep dependencies updated
3. Monitor for malicious uploads
4. Implement rate limiting

### Performance
1. Optimize image processing settings
2. Use appropriate storage backends
3. Implement caching strategies
4. Monitor resource usage

## Support and Maintenance

### Regular Tasks
- **Daily**: Monitor error logs and upload metrics
- **Weekly**: Review storage usage and cleanup temp files
- **Monthly**: Security audit and dependency updates
- **Quarterly**: Performance review and optimization

### Backup Strategy
- **Metadata**: Daily database backups
- **Files**: Incremental file backups
- **Configuration**: Version control for settings
- **Recovery**: Tested disaster recovery procedures
