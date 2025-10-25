package com.webrayan.bazaar.service;

import com.webrayan.bazaar.modules.ads.entity.AdCategory;
import com.webrayan.bazaar.modules.ads.repository.AdCategoryRepository;
import com.webrayan.bazaar.modules.ads.service.AdCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdAdCategoryServiceTest {

    @InjectMocks
    private AdCategoryService adCategoryService;

    @Mock
    private AdCategoryRepository adCategoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   // @Test
    void getAllCategories_ShouldReturnList() {
        AdCategory cat1 = new AdCategory();
        cat1.setId(1L);
        cat1.setName("Cat1");

        AdCategory cat2 = new AdCategory();
        cat2.setId(2L);
        cat2.setName("Cat2");

        when(adCategoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<AdCategory> categories = adCategoryService.getAllCategories();

        assertEquals(2, categories.size());
        verify(adCategoryRepository, times(1)).findAll();
    }

   // @Test
    void getCategoryById_ShouldReturnCategory_WhenFound() {
        AdCategory cat = new AdCategory();
        cat.setId(1L);
        cat.setName("Category");

        when(adCategoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        Optional<AdCategory> result = adCategoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Category", result.get().getName());
        verify(adCategoryRepository, times(1)).findById(1L);
    }

    //@Test
    void getCategoryById_ShouldReturnEmpty_WhenNotFound() {
        when(adCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AdCategory> result = adCategoryService.getCategoryById(1L);

        assertTrue(result.isEmpty());
        verify(adCategoryRepository, times(1)).findById(1L);
    }

    //@Test
    void createCategory_ShouldSaveCategory_WhenNameNotExists() {
        AdCategory cat = new AdCategory();
        cat.setName("NewCategory");

        when(adCategoryRepository.existsByName("NewCategory")).thenReturn(false);
        when(adCategoryRepository.save(cat)).thenReturn(cat);

        AdCategory saved = adCategoryService.createCategory(cat);

        assertNotNull(saved);
        assertEquals("NewCategory", saved.getName());
        verify(adCategoryRepository, times(1)).existsByName("NewCategory");
        verify(adCategoryRepository, times(1)).save(cat);
    }


    //@Test
    void createCategory_ShouldThrowException_WhenNameExists() {
        AdCategory cat = new AdCategory();
        cat.setName("ExistingCategory");

        when(adCategoryRepository.existsByName("ExistingCategory")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adCategoryService.createCategory(cat);
        });

        assertEquals("Category with the same name already exists.", exception.getMessage());
        verify(adCategoryRepository, times(1)).existsByName("ExistingCategory");
        verify(adCategoryRepository, never()).save(any());
    }

   // @Test
    void deleteCategory_ShouldCallDeleteById() {
        doNothing().when(adCategoryRepository).deleteById(1L);

        adCategoryService.deleteCategory(1L);

        verify(adCategoryRepository, times(1)).deleteById(1L);
    }
}
