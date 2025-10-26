package com.webrayan.store.controller;

import com.webrayan.store.modules.ads.controller.ImageController;
import com.webrayan.store.modules.ads.repository.ImageMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ImageControllerTest {

    @InjectMocks
    private ImageController imageController;

    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // ایجاد دایرکتوری برای ذخیره تصاویر در صورت عدم وجود
        new File("uploads/").mkdirs();
    }


    //@Test
    void testUploadImage() throws IOException {
//        MockMultipartFile file = new MockMultipartFile("file", "testImage.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
//
//        String response = imageController.uploadImage(file);
//
//        assertEquals("Images uploaded and resized successfully!", response);
//        verify(imageMetadataRepository, times(1)).save(any(Image.class));
//
//        // بررسی وجود فایل‌های ذخیره شده
//        assertTrue(Files.exists(Paths.get("uploads/original_testImage.jpg")));
//        assertTrue(Files.exists(Paths.get("uploads/150x150_testImage.jpg")));
//        assertTrue(Files.exists(Paths.get("uploads/300x300_testImage.jpg")));
//        assertTrue(Files.exists(Paths.get("uploads/600x600_testImage.jpg")));
    }

    //@Test
    void testViewImage_Original() throws IOException {
        // فرض کنید که تصویر اصلی قبلاً بارگذاری شده است
//        MockMultipartFile file = new MockMultipartFile("file", "testImage.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
//        imageController.uploadImage(file);
//
//        byte[] imageBytes = imageController.viewImage("original", "testImage.jpg");
//
//        assertNotNull(imageBytes);
//        assertEquals("test image content", new String(imageBytes));
    }

    //@Test
    void testViewImage_Resized() throws IOException {
        // فرض کنید که تصویر سایز شده قبلاً بارگذاری شده است
//        MockMultipartFile file = new MockMultipartFile("file", "testImage.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
//        imageController.uploadImage(file);
//
//        byte[] imageBytes = imageController.viewImage("150x150", "testImage.jpg");
//
//        assertNotNull(imageBytes);
//        assertEquals("test image content", new String(imageBytes));
    }

    //@Test
    void testViewImage_NotFound() {
        Exception exception = assertThrows(IOException.class, () -> {
            imageController.viewImage("original", "nonExistentImage.jpg");
        });

        assertEquals("File not found", exception.getMessage());
    }
}
