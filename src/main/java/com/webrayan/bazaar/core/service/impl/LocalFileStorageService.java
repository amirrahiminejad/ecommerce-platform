package com.webrayan.bazaar.core.service.impl;

import com.webrayan.bazaar.core.config.FileUploadConfig;
import com.webrayan.bazaar.core.service.FileStorageService;
import com.webrayan.bazaar.core.validation.FileValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Autowired
    private FileValidationService fileValidationService;

    private Path uploadPath;
    private Path tempPath;

    @PostConstruct
    public void init() {
        try {
            this.uploadPath = Paths.get(fileUploadConfig.getUploadDirectory()).toAbsolutePath().normalize();
            this.tempPath = Paths.get(fileUploadConfig.getTempDirectory()).toAbsolutePath().normalize();

            // Create directories if they don't exist
            Files.createDirectories(uploadPath);
            Files.createDirectories(tempPath);

            logger.info("File storage initialized - Upload path: {}, Temp path: {}", uploadPath, tempPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directories", e);
        }
    }

    @Override
    public FileStorageResult storeFile(MultipartFile file, String directory) throws IOException {
        // Validate file first
        FileValidationService.FileValidationResult validationResult = fileValidationService.validateFile(file);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("File validation failed: " + validationResult.getErrorMessage());
        }

        // Generate secure filename
        String secureFilename = fileValidationService.generateSecureFilename(file.getOriginalFilename());
        
        // Create target directory
        Path targetDirectory = uploadPath.resolve(directory);
        Files.createDirectories(targetDirectory);

        // Generate unique file reference
        String fileReference = UUID.randomUUID().toString() + "_" + secureFilename;
        Path targetLocation = targetDirectory.resolve(fileReference);

        // Ensure the target location is within the upload directory (security check)
        if (!targetLocation.normalize().startsWith(uploadPath)) {
            throw new SecurityException("Cannot store file outside upload directory");
        }

        try {
            // Copy file to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("File stored successfully: {} -> {}", file.getOriginalFilename(), targetLocation);

            return new FileStorageResult(
                fileReference,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                targetLocation.toString()
            );

        } catch (IOException e) {
            logger.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new IOException("Could not store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public List<FileStorageResult> storeFiles(List<MultipartFile> files, String directory) throws IOException {
        // Validate all files first
        FileValidationService.FileValidationResult validationResult = fileValidationService.validateFiles(files);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Files validation failed: " + validationResult.getErrorMessage());
        }

        List<FileStorageResult> results = new ArrayList<>();
        List<String> storedFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                FileStorageResult result = storeFile(file, directory);
                results.add(result);
                storedFiles.add(result.getFileReference());
            }
            return results;

        } catch (Exception e) {
            // Rollback: delete any files that were successfully stored
            logger.warn("Rolling back file storage due to error, deleting {} files", storedFiles.size());
            storedFiles.forEach(fileRef -> {
                try {
                    deleteFile(fileRef);
                } catch (IOException deleteError) {
                    logger.error("Failed to delete file during rollback: {}", fileRef, deleteError);
                }
            });
            throw e;
        }
    }

    @Override
    public byte[] retrieveFile(String fileReference) throws IOException {
        Path filePath = resolveFilePath(fileReference);
        
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + fileReference);
        }

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Failed to read file: {}", fileReference, e);
            throw new IOException("Could not read file: " + fileReference, e);
        }
    }

    @Override
    public boolean deleteFile(String fileReference) throws IOException {
        Path filePath = resolveFilePath(fileReference);
        
        if (!Files.exists(filePath)) {
            logger.warn("Attempted to delete non-existent file: {}", fileReference);
            return false;
        }

        try {
            Files.delete(filePath);
            logger.info("File deleted successfully: {}", fileReference);
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", fileReference, e);
            throw new IOException("Could not delete file: " + fileReference, e);
        }
    }

    @Override
    public boolean deleteFiles(List<String> fileReferences) throws IOException {
        boolean allDeleted = true;
        List<String> failedDeletions = new ArrayList<>();

        for (String fileReference : fileReferences) {
            try {
                if (!deleteFile(fileReference)) {
                    allDeleted = false;
                    failedDeletions.add(fileReference);
                }
            } catch (IOException e) {
                allDeleted = false;
                failedDeletions.add(fileReference);
                logger.error("Failed to delete file: {}", fileReference, e);
            }
        }

        if (!failedDeletions.isEmpty()) {
            logger.warn("Failed to delete {} files: {}", failedDeletions.size(), failedDeletions);
        }

        return allDeleted;
    }

    @Override
    public boolean fileExists(String fileReference) {
        try {
            Path filePath = resolveFilePath(fileReference);
            return Files.exists(filePath);
        } catch (Exception e) {
            logger.error("Error checking file existence: {}", fileReference, e);
            return false;
        }
    }

    @Override
    public FileMetadata getFileMetadata(String fileReference) throws IOException {
        Path filePath = resolveFilePath(fileReference);
        
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + fileReference);
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            
            return new FileMetadata(
                filePath.getFileName().toString(),
                attrs.size(),
                Files.probeContentType(filePath),
                LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault())
            );
        } catch (IOException e) {
            logger.error("Failed to get file metadata: {}", fileReference, e);
            throw new IOException("Could not get file metadata: " + fileReference, e);
        }
    }

    @Override
    public String moveFile(String fileReference, String newDirectory) throws IOException {
        Path sourcePath = resolveFilePath(fileReference);
        
        if (!Files.exists(sourcePath)) {
            throw new IOException("File not found: " + fileReference);
        }

        // Create target directory
        Path targetDirectory = uploadPath.resolve(newDirectory);
        Files.createDirectories(targetDirectory);

        // Generate new file reference
        String fileName = sourcePath.getFileName().toString();
        String newFileReference = UUID.randomUUID().toString() + "_" + fileName;
        Path targetPath = targetDirectory.resolve(newFileReference);

        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File moved successfully: {} -> {}", fileReference, newFileReference);
            return newFileReference;
        } catch (IOException e) {
            logger.error("Failed to move file: {} -> {}", fileReference, newDirectory, e);
            throw new IOException("Could not move file: " + fileReference, e);
        }
    }

    @Override
    public String copyFile(String fileReference, String newDirectory) throws IOException {
        Path sourcePath = resolveFilePath(fileReference);
        
        if (!Files.exists(sourcePath)) {
            throw new IOException("File not found: " + fileReference);
        }

        // Create target directory
        Path targetDirectory = uploadPath.resolve(newDirectory);
        Files.createDirectories(targetDirectory);

        // Generate new file reference
        String fileName = sourcePath.getFileName().toString();
        String newFileReference = UUID.randomUUID().toString() + "_" + fileName;
        Path targetPath = targetDirectory.resolve(newFileReference);

        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File copied successfully: {} -> {}", fileReference, newFileReference);
            return newFileReference;
        } catch (IOException e) {
            logger.error("Failed to copy file: {} -> {}", fileReference, newDirectory, e);
            throw new IOException("Could not copy file: " + fileReference, e);
        }
    }

    @Override
    public String getFileUrl(String fileReference) {
        // For local storage, return a relative URL that can be served by the application
        return "/api/files/download/" + fileReference;
    }

    @Override
    public void cleanupTempFiles() {
        try {
            if (!Files.exists(tempPath)) {
                return;
            }

            Files.walkFileTree(tempPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Delete files older than 24 hours
                    LocalDateTime fileTime = LocalDateTime.ofInstant(
                        attrs.creationTime().toInstant(), ZoneId.systemDefault());
                    
                    if (fileTime.isBefore(LocalDateTime.now().minusDays(1))) {
                        Files.delete(file);
                        logger.debug("Deleted temporary file: {}", file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    // Delete empty directories
                    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                        if (!dirStream.iterator().hasNext()) {
                            Files.delete(dir);
                            logger.debug("Deleted empty temporary directory: {}", dir);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            logger.info("Temporary files cleanup completed");
        } catch (IOException e) {
            logger.error("Failed to cleanup temporary files", e);
        }
    }

    /**
     * Resolve file reference to actual file path
     */
    private Path resolveFilePath(String fileReference) throws IOException {
        if (fileReference == null || fileReference.trim().isEmpty()) {
            throw new IllegalArgumentException("File reference cannot be null or empty");
        }

        // Extract directory and filename from file reference
        // File reference format: directory/filename or just filename
        Path filePath;
        if (fileReference.contains("/")) {
            filePath = uploadPath.resolve(fileReference);
        } else {
            // Search in all subdirectories
            filePath = findFileInDirectories(fileReference);
        }

        // Security check: ensure the resolved path is within upload directory
        if (!filePath.normalize().startsWith(uploadPath)) {
            throw new SecurityException("File reference points outside upload directory: " + fileReference);
        }

        return filePath;
    }

    /**
     * Find file in upload directories
     */
    private Path findFileInDirectories(String filename) throws IOException {
        try {
            return Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals(filename))
                .findFirst()
                .orElseThrow(() -> new IOException("File not found: " + filename));
        } catch (IOException e) {
            throw new IOException("Error searching for file: " + filename, e);
        }
    }
}
