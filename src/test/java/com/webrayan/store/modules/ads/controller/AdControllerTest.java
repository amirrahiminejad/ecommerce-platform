package com.webrayan.store.modules.ads.controller;

import com.webrayan.store.modules.ads.dto.AdRequestDto;
import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.service.AdServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AdControllerTest {

    @InjectMocks
    private AdController adController;

    @Mock
    private AdServiceImpl adService;

    private AdRequestDto adRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adRequestDto = new AdRequestDto();
        adRequestDto.setTitle("آگهی آزمایشی");
        adRequestDto.setPrice(10000.0);
        adRequestDto.setDescription("توضیحات");
        // سایر فیلدهای آگهی را تنظیم کنید
    }

    @Test
    void createAd() {
        Ad result = new Ad();
        when(adService.createAd(any(AdRequestDto.class))).thenReturn(result);
        Ad createdAd = adController.createAd(adRequestDto);
        assertNotNull(createdAd);
        assertNotEquals(null, createdAd);
        verify(adService, times(1)).createAd(any(AdRequestDto.class));
    }

    @Test
    void updateAd() {
//        when(adService.updateAd(anyLong(), any(Ad.class))).thenReturn(ad);
//
//        Ad updatedAd = adController.updateAd(1L, ad);
//
//        assertNotNull(updatedAd);
//        assertEquals(ad.getId(), updatedAd.getId());
//        verify(adService, times(1)).updateAd(anyLong(), any(Ad.class));
    }

    @Test
    void changeAdStatus() {
//        when(adService.changeStatus(anyLong(), any(AdStatus.class), any())).thenReturn(ad);
//
//        Ad changedAd = adController.changeAdStatus(1L, AdStatus.REJECTED, "Reason");
//
//        assertNotNull(changedAd);
//        assertEquals(ad.getId(), changedAd.getId());
//        verify(adService, times(1)).changeStatus(anyLong(), any(AdStatus.class), any());
    }

    @Test
    void getAdById() {
//        when(adService.getAdById(anyLong())).thenReturn(ad);
//
//        ResponseEntity<Ad> response = adController.getAdById(1L);
//
//        assertNotNull(response);
//        assertEquals(ad.getId(), response.getBody().getId());
//        verify(adService, times(1)).getAdById(anyLong());
    }

    @Test
    void getAdsByStatus() {
//        List<Ad> ads = Arrays.asList(ad);
//        when(adService.getAdsByStatus(any(AdStatus.class))).thenReturn(ads);
//
//        List<Ad> result = adController.getAdsByStatus(AdStatus.APPROVED);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(adService, times(1)).getAdsByStatus(any(AdStatus.class));
    }

    @Test
    void getAdsByUser() {
//        List<Ad> ads = Arrays.asList(ad);
//        when(adService.getAdsByUser(anyLong())).thenReturn(ads);
//
//        List<Ad> result = adController.getAdsByUser(1L);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(adService, times(1)).getAdsByUser(anyLong());
    }

    @Test
    void deleteAd() {
        doNothing().when(adService).deleteAd(anyLong());

        ResponseEntity<Void> response = adController.deleteAd(1L);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(adService, times(1)).deleteAd(anyLong());
    }
}
