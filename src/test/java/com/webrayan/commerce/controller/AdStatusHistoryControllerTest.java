package com.webrayan.commerce.controller;

import com.webrayan.commerce.modules.ads.controller.AdStatusHistoryController;
import com.webrayan.commerce.modules.ads.entity.AdStatusHistory;
import com.webrayan.commerce.modules.ads.service.AdStatusHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AdStatusHistoryControllerTest {

    @InjectMocks
    private AdStatusHistoryController adStatusHistoryController;

    @Mock
    private AdStatusHistoryService service;

    private AdStatusHistory adStatusHistory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adStatusHistory = new AdStatusHistory();
        adStatusHistory.setId(1L);
        // سایر فیلدهای آگهی را تنظیم کنید
    }

    @Test
    void getAdStatusHistory() {
        List<AdStatusHistory> historyList = Arrays.asList(adStatusHistory);
        when(service.getStatusHistoryByAdId(anyLong())).thenReturn(historyList);

        List<AdStatusHistory> result = adStatusHistoryController.getAdStatusHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adStatusHistory.getId(), result.get(0).getId());
        verify(service, times(1)).getStatusHistoryByAdId(anyLong());
    }

    @Test
    void saveAdStatusHistory() {
        when(service.saveStatusHistory(any(AdStatusHistory.class))).thenReturn(adStatusHistory);

        AdStatusHistory savedHistory = adStatusHistoryController.saveAdStatusHistory(adStatusHistory);

        assertNotNull(savedHistory);
        assertEquals(adStatusHistory.getId(), savedHistory.getId());
        verify(service, times(1)).saveStatusHistory(any(AdStatusHistory.class));
    }
}
