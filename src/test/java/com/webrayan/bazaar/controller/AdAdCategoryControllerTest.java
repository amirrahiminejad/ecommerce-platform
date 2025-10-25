package com.webrayan.bazaar.controller;

import com.webrayan.bazaar.modules.ads.controller.AdCategoryController;
import com.webrayan.bazaar.modules.ads.entity.AdCategory;
import com.webrayan.bazaar.modules.ads.service.AdCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdAdCategoryControllerTest {

    @InjectMocks
    private AdCategoryController adCategoryController;

    @Mock
    private AdCategoryService adCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        AdCategory adCategory1 = new AdCategory();
        AdCategory adCategory2 = new AdCategory();
        List<AdCategory> categories = Arrays.asList(adCategory1, adCategory2);

        when(adCategoryService.getAllCategories()).thenReturn(categories);

        List<AdCategory> result = adCategoryController.getAllCategories();

        assertEquals(2, result.size());
        verify(adCategoryService, times(1)).getAllCategories();
    }

    @Test
    void testGetCategoryById_Found() {
        Long id = 1L;
        AdCategory adCategory = new AdCategory();

        when(adCategoryService.getCategoryById(id)).thenReturn(Optional.of(adCategory));

        ResponseEntity<AdCategory> response = adCategoryController.getCategoryById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(adCategoryService, times(1)).getCategoryById(id);
    }

    @Test
    void testGetCategoryById_NotFound() {
        Long id = 1L;

        when(adCategoryService.getCategoryById(id)).thenReturn(Optional.empty());

        ResponseEntity<AdCategory> response = adCategoryController.getCategoryById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(adCategoryService, times(1)).getCategoryById(id);
    }

    @Test
    void testDeleteCategory() {
        Long id = 1L;

        adCategoryController.deleteCategory(id);

        verify(adCategoryService, times(1)).deleteCategory(id);
    }

    // Uncomment and implement this test when the createCategory method is available
    /*
    @Test
    void testCreateCategory() {
        Category category = new Category();
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        Category result = categoryController.createCategory(category);

        assertNotNull(result);
        verify(categoryService, times(1)).createCategory(category);
    }
    */
}
