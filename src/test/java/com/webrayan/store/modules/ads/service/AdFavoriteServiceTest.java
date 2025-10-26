package com.webrayan.store.service;

import com.webrayan.store.modules.ads.entity.AdFavorite;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.repository.FavoriteRepository;
import com.webrayan.store.modules.ads.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdFavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFavoritesByUser_ShouldReturnList() {
        User user = new User();
        user.setId(5l);

        AdFavorite fav1 = new AdFavorite();
        fav1.setId(1L);
        fav1.setUser(user);

        AdFavorite fav2 = new AdFavorite();
        fav2.setId(2L);
        fav2.setUser(user);

        when(favoriteRepository.findByUserId(5L)).thenReturn(List.of(fav1, fav2));

        List<AdFavorite> adFavorites = favoriteService.getFavoritesByUser(5L);

        assertEquals(2, adFavorites.size());
        assertEquals(user, adFavorites.get(0).getUser());
        verify(favoriteRepository, times(1)).findByUserId(5L);
    }

    @Test
    void addFavorite_ShouldSaveAndReturnFavorite() {
        User user = new User();
        user.setId(5l);
        Ad ad = new Ad();
        ad.setId(10L);

        AdFavorite adFavorite = new AdFavorite();
        adFavorite.setUser(user);
        adFavorite.setAd(ad);

        when(favoriteRepository.save(adFavorite)).thenReturn(adFavorite);

        AdFavorite saved = favoriteService.addFavorite(adFavorite);

        assertNotNull(saved);
        assertEquals(user, saved.getUser());
        assertEquals(ad, saved.getAd());
        verify(favoriteRepository, times(1)).save(adFavorite);
    }

    @Test
    void removeFavorite_ShouldCallDeleteById() {
        doNothing().when(favoriteRepository).deleteById(1L);

        favoriteService.removeFavorite(1L);

        verify(favoriteRepository, times(1)).deleteById(1L);
    }
}
