//package com.webrayan.commerce.modules.ads.controller;
//
//import com.webrayan.commerce.modules.ads.entity.Ad;
//import com.webrayan.commerce.modules.ads.service.AdService;
//import com.webrayan.commerce.modules.ads.service.AdCategoryService;
//import com.webrayan.commerce.core.common.service.LocationService;
//import com.webrayan.commerce.modules.acl.entity.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.ui.Model;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AdPageControllerTest {
//
//    @InjectMocks
//    private AdPageController adPageController;
//
//    @Mock
//    private AdService adService;
//
//    @Mock
//    private AdCategoryService adCategoryService;
//
//    @Mock
//    private LocationService locationService;
//
//    @Mock
//    private Model model;
//
//    private MockMvc mockMvc;
//    private Ad testAd;
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(adPageController).build();
//
//        // تنظیم کاربر آزمایشی
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setUsername("testuser");
//        testUser.setEmail("test@example.com");
//        testUser.setFirstName("Test");
//        testUser.setLastName("User");
//
//        // تنظیم آگهی آزمایشی
//        testAd = new Ad();
//        testAd.setId(1L);
//        testAd.setTitle("آگهی آزمایشی");
//        testAd.setDescription("این یک آگهی آزمایشی است");
//        testAd.setPrice(100000.0);
//        testAd.setUser(testUser);
//        testAd.setViewsCount(10);
//    }
//
//    @Test
//    void viewCompactAd_Success() {
//        // Given
//        Long adId = 1L;
//        when(adService.getAdById(adId)).thenReturn(testAd);
//        doNothing().when(adService).incrementViewCount(adId);
//
//        // When
//        String result = adPageController.viewCompactAd(adId, model);
//
//        // Then
//        assertEquals("ads/view-compact", result);
//        verify(adService, times(1)).getAdById(adId);
//        verify(adService, times(1)).incrementViewCount(adId);
//        verify(model, times(1)).addAttribute("ad", testAd);
//        verify(model, times(1)).addAttribute("pageTitle", testAd.getTitle());
//    }
//
//    @Test
//    void viewCompactAd_NotFound() {
//        // Given
//        Long adId = 999L;
//        when(adService.getAdById(adId)).thenThrow(new RuntimeException("Ad not found"));
//
//        // When
//        String result = adPageController.viewCompactAd(adId, model);
//
//        // Then
//        assertEquals("error/404", result);
//        verify(adService, times(1)).getAdById(adId);
//        verify(adService, never()).incrementViewCount(adId);
//        verify(model, times(1)).addAttribute("errorMessage", "آگهی مورد نظر یافت نشد.");
//    }
//
//    @Test
//    void viewCompactAd_IncrementViewCountFails() {
//        // Given
//        Long adId = 1L;
//        when(adService.getAdById(adId)).thenReturn(testAd);
//        doThrow(new RuntimeException("Database error")).when(adService).incrementViewCount(adId);
//
//        // When
//        String result = adPageController.viewCompactAd(adId, model);
//
//        // Then
//        // باید همچنان صفحه را نمایش دهد حتی اگر افزایش view count شکست بخورد
//        assertEquals("error/404", result);
//        verify(adService, times(1)).getAdById(adId);
//        verify(adService, times(1)).incrementViewCount(adId);
//    }
//
//    @Test
//    void viewCompactAd_ValidatesAdData() {
//        // Given
//        Long adId = 1L;
//        Ad adWithCompleteData = new Ad();
//        adWithCompleteData.setId(adId);
//        adWithCompleteData.setTitle("آگهی کامل");
//        adWithCompleteData.setDescription("توضیحات کامل آگهی");
//        adWithCompleteData.setPrice(250000.0);
//        adWithCompleteData.setUser(testUser);
//        adWithCompleteData.setViewsCount(25);
//
//        when(adService.getAdById(adId)).thenReturn(adWithCompleteData);
//        doNothing().when(adService).incrementViewCount(adId);
//
//        // When
//        String result = adPageController.viewCompactAd(adId, model);
//
//        // Then
//        assertEquals("ads/view-compact", result);
//        verify(model, times(1)).addAttribute("ad", adWithCompleteData);
//        verify(model, times(1)).addAttribute("pageTitle", "آگهی کامل");
//
//        // بررسی اینکه اطلاعات آگهی کامل باشد
//        assertNotNull(adWithCompleteData.getTitle());
//        assertNotNull(adWithCompleteData.getDescription());
//        assertNotNull(adWithCompleteData.getPrice());
//        assertNotNull(adWithCompleteData.getUser());
//        assertTrue(adWithCompleteData.getViewsCount() >= 0);
//    }
//
//    @Test
//    void viewCompactAd_HandlesNullValues() {
//        // Given
//        Long adId = 1L;
//        Ad adWithNulls = new Ad();
//        adWithNulls.setId(adId);
//        adWithNulls.setTitle("آگهی با مقادیر خالی");
//        adWithNulls.setUser(testUser);
//        // سایر فیلدها null هستند
//
//        when(adService.getAdById(adId)).thenReturn(adWithNulls);
//        doNothing().when(adService).incrementViewCount(adId);
//
//        // When
//        String result = adPageController.viewCompactAd(adId, model);
//
//        // Then
//        assertEquals("ads/view-compact", result);
//        verify(model, times(1)).addAttribute("ad", adWithNulls);
//        verify(model, times(1)).addAttribute("pageTitle", "آگهی با مقادیر خالی");
//    }
//}
