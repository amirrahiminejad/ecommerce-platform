package com.webrayan.store.controller;

import com.webrayan.store.modules.ads.controller.AdReviewController;
import com.webrayan.store.modules.ads.entity.AdReview;
import com.webrayan.store.modules.ads.service.AdReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AdReviewControllerTest {

    @InjectMocks
    private AdReviewController adReviewController;

    @Mock
    private AdReviewService adReviewService;

    private AdReview adReview;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adReview = new AdReview();
        adReview.setId(1L);
        // سایر فیلدهای آگهی را تنظیم کنید
    }

    @Test
    void getAllReviews() {
        List<AdReview> reviews = Arrays.asList(adReview);
        when(adReviewService.getAllReviews()).thenReturn(reviews);

        ResponseEntity<List<AdReview>> response = adReviewController.getAllReviews();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(adReviewService, times(1)).getAllReviews();
    }

    @Test
    void getReviewsByAdId() {
        List<AdReview> reviews = Arrays.asList(adReview);
        when(adReviewService.getReviewsByAdId(anyLong())).thenReturn(reviews);

        ResponseEntity<List<AdReview>> response = adReviewController.getReviewsByAdId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(adReviewService, times(1)).getReviewsByAdId(anyLong());
    }

    @Test
    void getReviewsByUserId() {
        List<AdReview> reviews = Arrays.asList(adReview);
        when(adReviewService.getReviewsByUserId(anyLong())).thenReturn(reviews);

        ResponseEntity<List<AdReview>> response = adReviewController.getReviewsByUserId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(adReviewService, times(1)).getReviewsByUserId(anyLong());
    }

    @Test
    void createReview() {
        when(adReviewService.saveReview(any(AdReview.class))).thenReturn(adReview);

        ResponseEntity<AdReview> response = adReviewController.createReview(adReview);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(adReview.getId(), response.getBody().getId());
        verify(adReviewService, times(1)).saveReview(any(AdReview.class));
    }

    @Test
    void deleteReview() {
        doNothing().when(adReviewService).deleteReview(anyLong());

        ResponseEntity<Void> response = adReviewController.deleteReview(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adReviewService, times(1)).deleteReview(anyLong());
    }
}
