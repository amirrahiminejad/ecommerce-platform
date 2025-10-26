//package com.webrayan.store.service;
//
//import com.webrayan.store.entity.Ad;
//import com.webrayan.store.entity.Location;
//import com.webrayan.store.enums.AdStatus;
//import com.webrayan.store.repository.AdRepository;
//import com.webrayan.store.repository.CategoryRepository;
//import com.webrayan.store.acl.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import jakarta.persistence.EntityNotFoundException;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class AdServiceImplTest {
//
//    @Mock
//    private AdRepository adRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @InjectMocks
//    private AdServiceImpl adService;
//
//    private Ad ad;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        ad = new Ad();
//        ad.setId(1L);
//        ad.setTitle("Test Ad");
//        ad.setPrice(100.0);
//        ad.setCategory(null); // Set a valid category as needed
//        ad.setLocation(null);
//    }
//
//    @Test
//    void createAd_ShouldSaveAd_WhenAdIsValid() {
//        when(adRepository.save(any(Ad.class))).thenReturn(ad);
//
//        Ad createdAd = adService.createAd(ad);
//
//        assertNotNull(createdAd);
//        assertEquals("Test Ad", createdAd.getTitle());
//        verify(adRepository, times(1)).save(ad);
//    }
//
//    @Test
//    void createAd_ShouldThrowException_WhenTitleIsNull() {
//        ad.setTitle(null);
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            adService.createAd(ad);
//        });
//
//        assertEquals("Ad title is required", exception.getMessage());
//    }
//
//    @Test
//    void updateAd_ShouldUpdateAd_WhenAdExists() {
//        when(adRepository.findById(1L)).thenReturn(Optional.of(ad));
//        when(adRepository.save(any(Ad.class))).thenReturn(ad);
//
//        Ad updatedAd = new Ad();
//        updatedAd.setTitle("Updated Ad");
//        updatedAd.setPrice(150.0);
//        updatedAd.setCategory(null); // Set a valid category as needed
//        updatedAd.setLocation(null);
//
//        Ad result = adService.updateAd(1L, updatedAd);
//
//        assertEquals("Updated Ad", result.getTitle());
//        verify(adRepository, times(1)).save(any(Ad.class));
//    }
//
//    @Test
//    void updateAd_ShouldThrowException_WhenAdDoesNotExist() {
//        when(adRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
//            adService.updateAd(1L, ad);
//        });
//
//        assertEquals("Ad not found with id: 1", exception.getMessage());
//    }
//
//    @Test
//    void deleteAd_ShouldDeleteAd_WhenAdExists() {
//        when(adRepository.existsById(1L)).thenReturn(true);
//
//        adService.deleteAd(1L);
//
//        verify(adRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    void deleteAd_ShouldThrowException_WhenAdDoesNotExist() {
//        when(adRepository.existsById(1L)).thenReturn(false);
//
//        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
//            adService.deleteAd(1L);
//        });
//
//        assertEquals("Ad not found with id: 1", exception.getMessage());
//    }
//
//    // سایر تست‌ها برای متدهای دیگر مانند changeStatus و getAdById و ...
//}
