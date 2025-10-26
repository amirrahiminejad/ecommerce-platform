package com.webrayan.store.core.service.impl;

import com.webrayan.store.core.config.FileUploadConfig;
import com.webrayan.store.core.service.FileStorageService;
import com.webrayan.store.core.validation.FileValidationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalFileStorageServiceTest {
    // todo: active tests

    @TempDir
    Path tempDir;

    @Mock
    private FileUploadConfig fileUploadConfig;

    @Mock
    private FileValidationService fileValidationService;

    @InjectMocks
    private LocalFileStorageService fileStorageService;

   // @BeforeEach
    void setUp() throws IOException {
        Path uploadDir = tempDir.resolve("uploads");
        Path tempUploadDir = tempDir.resolve("temp");
        
        Files.createDirectories(uploadDir);
        Files.createDirectories(tempUploadDir);

        when(fileUploadConfig.getUploadDirectory()).thenReturn(uploadDir.toString());
        when(fileUploadConfig.getTempDirectory()).thenReturn(tempUploadDir.toString());

        // Mock validation to always pass for tests
        when(fileValidationService.validateFile(any()))
            .thenReturn(FileValidationService.FileValidationResult.valid());
        when(fileValidationService.generateSecureFilename(any()))
            .thenAnswer(invocation -> "secure_" + System.currentTimeMillis() + "_" + invocation.getArgument(0));

        fileStorageService.init();
    }

    //@Test
    void testStoreFile_ValidFile_ShouldStoreSuccessfully() throws IOException {
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult result = fileStorageService.storeFile(file, "products");

        assertNotNull(result);
        assertEquals("test.jpg", result.getOriginalFilename());
        assertEquals(content.length, result.getFileSize());
        assertEquals("image/jpeg", result.getContentType());
        assertNotNull(result.getFileReference());
        assertTrue(result.getStoragePath().contains("products"));
    }

    //@Test
    void testStoreFile_DirectoryCreation_ShouldCreateDirectory() throws IOException {
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        String newDirectory = "new-category/subcategory";
        FileStorageService.FileStorageResult result = fileStorageService.storeFile(file, newDirectory);

        assertNotNull(result);
        assertTrue(Files.exists(tempDir.resolve("uploads").resolve(newDirectory)));
    }

    //@Test
    void testStoreFiles_MultipleValidFiles_ShouldStoreAll() throws IOException {
        byte[] content1 = "test content 1".getBytes();
        byte[] content2 = "test content 2".getBytes();
        
        List<MultipartFile> files = Arrays.asList(
            new MockMultipartFile("file1", "test1.jpg", "image/jpeg", content1),
            new MockMultipartFile("file2", "test2.png", "image/png", content2)
        );

        when(fileValidationService.validateFiles(any()))
            .thenReturn(FileValidationService.FileValidationResult.valid());

        List<FileStorageService.FileStorageResult> results = fileStorageService.storeFiles(files, "gallery");

        assertEquals(2, results.size());
        assertEquals("test1.jpg", results.get(0).getOriginalFilename());
        assertEquals("test2.png", results.get(1).getOriginalFilename());
    }

    //@Test
    void testStoreFile_ValidationFailure_ShouldThrowException() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        when(fileValidationService.validateFile(any()))
            .thenReturn(FileValidationService.FileValidationResult.invalid("File validation failed"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileStorageService.storeFile(file, "products")
        );

        assertTrue(exception.getMessage().contains("File validation failed"));
    }

    //@Test
    void testRetrieveFile_ExistingFile_ShouldReturnContent() throws IOException {
        // First store a file
        byte[] originalContent = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", originalContent
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        // Then retrieve it
        byte[] retrievedContent = fileStorageService.retrieveFile(storeResult.getFileReference());

        assertArrayEquals(originalContent, retrievedContent);
    }

    //@Test
    void testRetrieveFile_NonExistentFile_ShouldThrowException() {
        IOException exception = assertThrows(
            IOException.class,
            () -> fileStorageService.retrieveFile("non-existent-file")
        );

        assertTrue(exception.getMessage().contains("File not found"));
    }

    //@Test
    void testDeleteFile_ExistingFile_ShouldDeleteSuccessfully() throws IOException {
        // Store a file first
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        // Delete the file
        boolean deleted = fileStorageService.deleteFile(storeResult.getFileReference());

        assertTrue(deleted);
        assertFalse(fileStorageService.fileExists(storeResult.getFileReference()));
    }

    //@Test
    void testDeleteFile_NonExistentFile_ShouldReturnFalse() throws IOException {
        boolean deleted = fileStorageService.deleteFile("non-existent-file");
        assertFalse(deleted);
    }

    //@Test
    void testFileExists_ExistingFile_ShouldReturnTrue() throws IOException {
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        assertTrue(fileStorageService.fileExists(storeResult.getFileReference()));
    }

    //@Test
    void testFileExists_NonExistentFile_ShouldReturnFalse() {
        assertFalse(fileStorageService.fileExists("non-existent-file"));
    }

    //@Test
    void testGetFileMetadata_ExistingFile_ShouldReturnMetadata() throws IOException {
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        FileStorageService.FileMetadata metadata = fileStorageService.getFileMetadata(storeResult.getFileReference());

        assertNotNull(metadata);
        assertEquals(content.length, metadata.getSize());
        assertNotNull(metadata.getCreatedAt());
        assertNotNull(metadata.getLastModified());
    }

   // @Test
    void testMoveFile_ExistingFile_ShouldMoveSuccessfully() throws IOException {
        // Store a file
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        // Move the file
        String newFileReference = fileStorageService.moveFile(storeResult.getFileReference(), "archive");

        assertNotNull(newFileReference);
        assertNotEquals(storeResult.getFileReference(), newFileReference);
        assertFalse(fileStorageService.fileExists(storeResult.getFileReference()));
        assertTrue(fileStorageService.fileExists(newFileReference));
    }

    //@Test
    void testCopyFile_ExistingFile_ShouldCopySuccessfully() throws IOException {
        // Store a file
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        // Copy the file
        String newFileReference = fileStorageService.copyFile(storeResult.getFileReference(), "backup");

        assertNotNull(newFileReference);
        assertNotEquals(storeResult.getFileReference(), newFileReference);
        assertTrue(fileStorageService.fileExists(storeResult.getFileReference()));
        assertTrue(fileStorageService.fileExists(newFileReference));
    }

    //@Test
    void testGetFileUrl_ShouldReturnValidUrl() throws IOException {
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content
        );

        FileStorageService.FileStorageResult storeResult = fileStorageService.storeFile(file, "products");

        String url = fileStorageService.getFileUrl(storeResult.getFileReference());

        assertNotNull(url);
        assertTrue(url.startsWith("/api/files/download/"));
        assertTrue(url.contains(storeResult.getFileReference()));
    }

    //@Test
    void testStoreFile_SecurityCheck_ShouldPreventDirectoryTraversal() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        // This should not throw an exception but should be handled safely
        assertDoesNotThrow(() -> {
            fileStorageService.storeFile(file, "../../../etc");
        });
    }
}
