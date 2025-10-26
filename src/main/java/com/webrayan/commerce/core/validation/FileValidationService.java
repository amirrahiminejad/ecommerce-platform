package com.webrayan.commerce.core.validation;

import com.webrayan.commerce.core.config.FileUploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class FileValidationService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * Validate uploaded file for security and format compliance
     */
    public FileValidationResult validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return FileValidationResult.invalid("File is empty or null");
        }

        // Check file size
        if (file.getSize() > fileUploadConfig.getMaxFileSize()) {
            return FileValidationResult.invalid(
                String.format("File size %d bytes exceeds maximum allowed size %d bytes", 
                    file.getSize(), fileUploadConfig.getMaxFileSize())
            );
        }

        // Check file type by MIME type
        String contentType = file.getContentType();
        if (contentType == null || !fileUploadConfig.getAllowedImageTypes().contains(contentType.toLowerCase())) {
            return FileValidationResult.invalid(
                String.format("File type '%s' not allowed. Allowed types: %s", 
                    contentType, String.join(", ", fileUploadConfig.getAllowedImageTypes()))
            );
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return FileValidationResult.invalid("Original filename is null");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!fileUploadConfig.getAllowedImageExtensions().contains(fileExtension)) {
            return FileValidationResult.invalid(
                String.format("File extension '%s' not allowed. Allowed extensions: %s", 
                    fileExtension, String.join(", ", fileUploadConfig.getAllowedImageExtensions()))
            );
        }

        // Check filename for malicious patterns
        if (containsMaliciousPatterns(originalFilename)) {
            return FileValidationResult.invalid("Filename contains potentially malicious patterns");
        }

        // Validate file header (magic bytes)
        try {
            if (!isValidImageFile(file)) {
                return FileValidationResult.invalid("File header validation failed - not a valid image file");
            }
        } catch (IOException e) {
            return FileValidationResult.invalid("Error reading file for validation: " + e.getMessage());
        }

        return FileValidationResult.valid();
    }

    /**
     * Validate multiple files
     */
    public FileValidationResult validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return FileValidationResult.invalid("No files provided");
        }

        if (files.size() > fileUploadConfig.getMaxFilesPerRequest()) {
            return FileValidationResult.invalid(
                String.format("Too many files. Maximum %d files allowed per request", 
                    fileUploadConfig.getMaxFilesPerRequest())
            );
        }

        long totalSize = files.stream()
            .filter(Objects::nonNull)
            .mapToLong(MultipartFile::getSize)
            .sum();

        if (totalSize > fileUploadConfig.getMaxFileSize() * files.size()) {
            return FileValidationResult.invalid("Total file size exceeds maximum allowed");
        }

        for (MultipartFile file : files) {
            FileValidationResult result = validateFile(file);
            if (!result.isValid()) {
                return result;
            }
        }

        return FileValidationResult.valid();
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * Check for malicious patterns in filename
     */
    private boolean containsMaliciousPatterns(String filename) {
        String[] maliciousPatterns = {
            "..", "/", "\\", ":", "*", "?", "\"", "<", ">", "|",
            "script", "javascript", "vbscript", "onload", "onerror"
        };

        String lowerFilename = filename.toLowerCase();
        return Arrays.stream(maliciousPatterns)
            .anyMatch(lowerFilename::contains);
    }

    /**
     * Validate file by checking magic bytes (file signature)
     */
    private boolean isValidImageFile(MultipartFile file) throws IOException {
        byte[] fileHeader = new byte[12];
        int bytesRead = file.getInputStream().read(fileHeader);
        
        if (bytesRead < 4) {
            return false;
        }

        // Check for common image file signatures
        return isJPEG(fileHeader) || isPNG(fileHeader) || isGIF(fileHeader) || isWebP(fileHeader);
    }

    private boolean isJPEG(byte[] header) {
        return header.length >= 4 &&
               (header[0] & 0xFF) == 0xFF &&
               (header[1] & 0xFF) == 0xD8 &&
               (header[2] & 0xFF) == 0xFF;
    }

    private boolean isPNG(byte[] header) {
        return header.length >= 8 &&
               (header[0] & 0xFF) == 0x89 &&
               (header[1] & 0xFF) == 0x50 &&
               (header[2] & 0xFF) == 0x4E &&
               (header[3] & 0xFF) == 0x47 &&
               (header[4] & 0xFF) == 0x0D &&
               (header[5] & 0xFF) == 0x0A &&
               (header[6] & 0xFF) == 0x1A &&
               (header[7] & 0xFF) == 0x0A;
    }

    private boolean isGIF(byte[] header) {
        return header.length >= 6 &&
               (header[0] & 0xFF) == 0x47 && // G
               (header[1] & 0xFF) == 0x49 && // I
               (header[2] & 0xFF) == 0x46 && // F
               (header[3] & 0xFF) == 0x38 && // 8
               ((header[4] & 0xFF) == 0x37 || (header[4] & 0xFF) == 0x39) && // 7 or 9
               (header[5] & 0xFF) == 0x61; // a
    }

    private boolean isWebP(byte[] header) {
        return header.length >= 12 &&
               (header[0] & 0xFF) == 0x52 && // R
               (header[1] & 0xFF) == 0x49 && // I
               (header[2] & 0xFF) == 0x46 && // F
               (header[3] & 0xFF) == 0x46 && // F
               (header[8] & 0xFF) == 0x57 && // W
               (header[9] & 0xFF) == 0x45 && // E
               (header[10] & 0xFF) == 0x42 && // B
               (header[11] & 0xFF) == 0x50; // P
    }

    /**
     * Generate secure filename
     */
    public String generateSecureFilename(String originalFilename) {
        if (originalFilename == null) {
            return "unnamed_" + System.currentTimeMillis();
        }

        String extension = getFileExtension(originalFilename);
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        
        // Remove dangerous characters and normalize
        String safeName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_")
                                  .replaceAll("_{2,}", "_")
                                  .toLowerCase();
        
        // Add timestamp to ensure uniqueness
        return safeName + "_" + System.currentTimeMillis() + extension;
    }

    /**
     * Validation result class
     */
    public static class FileValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private FileValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static FileValidationResult valid() {
            return new FileValidationResult(true, null);
        }

        public static FileValidationResult invalid(String errorMessage) {
            return new FileValidationResult(false, errorMessage);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
