package com.webrayan.commerce.core.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface for file storage operations
 * Supports multiple storage backends (local, cloud, etc.)
 */
public interface FileStorageService {

    /**
     * Store a file and return its storage reference
     */
    FileStorageResult storeFile(MultipartFile file, String directory) throws IOException;

    /**
     * Store multiple files
     */
    List<FileStorageResult> storeFiles(List<MultipartFile> files, String directory) throws IOException;

    /**
     * Retrieve file as byte array
     */
    byte[] retrieveFile(String fileReference) throws IOException;

    /**
     * Delete file by reference
     */
    boolean deleteFile(String fileReference) throws IOException;

    /**
     * Delete multiple files
     */
    boolean deleteFiles(List<String> fileReferences) throws IOException;

    /**
     * Check if file exists
     */
    boolean fileExists(String fileReference);

    /**
     * Get file metadata
     */
    FileMetadata getFileMetadata(String fileReference) throws IOException;

    /**
     * Move file to different directory
     */
    String moveFile(String fileReference, String newDirectory) throws IOException;

    /**
     * Copy file to different location
     */
    String copyFile(String fileReference, String newDirectory) throws IOException;

    /**
     * Get file URL for external access
     */
    String getFileUrl(String fileReference);

    /**
     * Clean up temporary files
     */
    void cleanupTempFiles();

    /**
     * Storage result class
     */
    class FileStorageResult {
        private final String fileReference;
        private final String originalFilename;
        private final long fileSize;
        private final String contentType;
        private final String storagePath;

        public FileStorageResult(String fileReference, String originalFilename, 
                               long fileSize, String contentType, String storagePath) {
            this.fileReference = fileReference;
            this.originalFilename = originalFilename;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.storagePath = storagePath;
        }

        public String getFileReference() { return fileReference; }
        public String getOriginalFilename() { return originalFilename; }
        public long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
        public String getStoragePath() { return storagePath; }
    }

    /**
     * File metadata class
     */
    class FileMetadata {
        private final String filename;
        private final long size;
        private final String contentType;
        private final java.time.LocalDateTime createdAt;
        private final java.time.LocalDateTime lastModified;

        public FileMetadata(String filename, long size, String contentType, 
                          java.time.LocalDateTime createdAt, java.time.LocalDateTime lastModified) {
            this.filename = filename;
            this.size = size;
            this.contentType = contentType;
            this.createdAt = createdAt;
            this.lastModified = lastModified;
        }

        public String getFilename() { return filename; }
        public long getSize() { return size; }
        public String getContentType() { return contentType; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getLastModified() { return lastModified; }
    }
}
