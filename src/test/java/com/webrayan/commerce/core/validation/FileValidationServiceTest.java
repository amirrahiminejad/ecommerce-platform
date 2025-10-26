package com.webrayan.commerce.core.validation;

import com.webrayan.commerce.core.config.FileUploadConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileValidationServiceTest {
    // todo: active tests

    @Mock
    private FileUploadConfig fileUploadConfig;

    @InjectMocks
    private FileValidationService fileValidationService;

  //  @BeforeEach
    void setUp() {
        when(fileUploadConfig.getMaxFileSize()).thenReturn(10485760L); // 10MB
        when(fileUploadConfig.getMaxFilesPerRequest()).thenReturn(5);
        when(fileUploadConfig.getAllowedImageTypes()).thenReturn(
            Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp")
        );
        when(fileUploadConfig.getAllowedImageExtensions()).thenReturn(
            Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp")
        );
    }

  //  @Test
    void testValidateFile_ValidJpegFile_ShouldPass() {
        // Create a mock JPEG file with valid header
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", jpegHeader
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

 //   @Test
    void testValidateFile_ValidPngFile_ShouldPass() {
        // Create a mock PNG file with valid header
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.png", "image/png", pngHeader
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

  //  @Test
    void testValidateFile_EmptyFile_ShouldFail() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", new byte[0]
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertFalse(result.isValid());
        assertEquals("File is empty or null", result.getErrorMessage());
    }

 //   @Test
    void testValidateFile_NullFile_ShouldFail() {
        FileValidationService.FileValidationResult result = fileValidationService.validateFile(null);

        assertFalse(result.isValid());
        assertEquals("File is empty or null", result.getErrorMessage());
    }

   // @Test
    void testValidateFile_FileTooLarge_ShouldFail() {
        byte[] largeContent = new byte[(int) (fileUploadConfig.getMaxFileSize() + 1)];
        MockMultipartFile file = new MockMultipartFile(
            "file", "large.jpg", "image/jpeg", largeContent
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("exceeds maximum allowed size"));
    }

   // @Test
    void testValidateFile_InvalidMimeType_ShouldFail() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "text/plain", jpegHeader
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("File type 'text/plain' not allowed"));
    }

  // @Test
    void testValidateFile_InvalidExtension_ShouldFail() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "image/jpeg", jpegHeader
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("File extension '.txt' not allowed"));
    }

 //   @Test
    void testValidateFile_MaliciousFilename_ShouldFail() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MockMultipartFile file = new MockMultipartFile(
            "file", "../../../etc/passwd.jpg", "image/jpeg", jpegHeader
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("malicious patterns"));
    }

   // @Test
    void testValidateFile_InvalidFileHeader_ShouldFail() {
        byte[] invalidHeader = {0x00, 0x00, 0x00, 0x00};
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", invalidHeader
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFile(file);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("File header validation failed"));
    }

  //  @Test
    void testValidateFiles_ValidFiles_ShouldPass() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        
        List<MultipartFile> files = Arrays.asList(
            new MockMultipartFile("file1", "test1.jpg", "image/jpeg", jpegHeader),
            new MockMultipartFile("file2", "test2.png", "image/png", pngHeader)
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFiles(files);

        assertTrue(result.isValid());
    }

 //   @Test
    void testValidateFiles_TooManyFiles_ShouldFail() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        List<MultipartFile> files = Arrays.asList(
            new MockMultipartFile("file1", "test1.jpg", "image/jpeg", jpegHeader),
            new MockMultipartFile("file2", "test2.jpg", "image/jpeg", jpegHeader),
            new MockMultipartFile("file3", "test3.jpg", "image/jpeg", jpegHeader),
            new MockMultipartFile("file4", "test4.jpg", "image/jpeg", jpegHeader),
            new MockMultipartFile("file5", "test5.jpg", "image/jpeg", jpegHeader),
            new MockMultipartFile("file6", "test6.jpg", "image/jpeg", jpegHeader)
        );

        FileValidationService.FileValidationResult result = fileValidationService.validateFiles(files);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Too many files"));
    }

   // @Test
    void testValidateFiles_EmptyList_ShouldFail() {
        FileValidationService.FileValidationResult result = fileValidationService.validateFiles(Arrays.asList());

        assertFalse(result.isValid());
        assertEquals("No files provided", result.getErrorMessage());
    }

 //   @Test
    void testGenerateSecureFilename_ValidFilename_ShouldSanitize() {
        String originalFilename = "My Test Image!@#$.jpg";
        String secureFilename = fileValidationService.generateSecureFilename(originalFilename);

        assertNotNull(secureFilename);
        assertTrue(secureFilename.endsWith(".jpg"));
        assertFalse(secureFilename.contains("!"));
        assertFalse(secureFilename.contains("@"));
        assertFalse(secureFilename.contains("#"));
        assertFalse(secureFilename.contains("$"));
        assertTrue(secureFilename.contains("_"));
    }

 //  @Test
    void testGenerateSecureFilename_NullFilename_ShouldGenerateDefault() {
        String secureFilename = fileValidationService.generateSecureFilename(null);

        assertNotNull(secureFilename);
        assertTrue(secureFilename.startsWith("unnamed_"));
    }

 //   @Test
    void testGenerateSecureFilename_DangerousFilename_ShouldSanitize() {
        String originalFilename = "../../../etc/passwd.jpg";
        String secureFilename = fileValidationService.generateSecureFilename(originalFilename);

        assertNotNull(secureFilename);
        assertFalse(secureFilename.contains("../"));
        assertFalse(secureFilename.contains("/"));
        assertTrue(secureFilename.endsWith(".jpg"));
    }
}
