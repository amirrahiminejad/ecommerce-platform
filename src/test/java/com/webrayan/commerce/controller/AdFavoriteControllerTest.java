package com.webrayan.commerce.controller;

import com.webrayan.commerce.modules.ads.controller.FavoriteController;
import com.webrayan.commerce.modules.ads.entity.AdFavorite;
import com.webrayan.commerce.modules.ads.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdFavoriteControllerTest {

    @InjectMocks
    private FavoriteController favoriteController;

    @Mock
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFavoritesByUser() {
        Long userId = 1L;
        AdFavorite adFavorite1 = new AdFavorite();
        AdFavorite adFavorite2 = new AdFavorite();
        List<AdFavorite> adFavorites = Arrays.asList(adFavorite1, adFavorite2);

        when(favoriteService.getFavoritesByUser(userId)).thenReturn(adFavorites);

        List<AdFavorite> result = favoriteController.getFavoritesByUser(userId);

        assertEquals(2, result.size());
        verify(favoriteService, times(1)).getFavoritesByUser(userId);
    }

    @Test
    void testAddFavorite() {
        AdFavorite adFavorite = new AdFavorite();
        when(favoriteService.addFavorite(any(AdFavorite.class))).thenReturn(adFavorite);

        AdFavorite result = favoriteController.addFavorite(adFavorite);

        assertNotNull(result);
        verify(favoriteService, times(1)).addFavorite(adFavorite);
    }

    @Test
    void testRemoveFavorite() {
        Long id = 1L;

        favoriteController.removeFavorite(id);

        verify(favoriteService, times(1)).removeFavorite(id);
    }
}
