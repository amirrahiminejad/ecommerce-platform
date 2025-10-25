package com.webrayan.bazaar.controller;

import com.webrayan.bazaar.modules.ads.controller.AdCategoryAttributeController;
import com.webrayan.bazaar.modules.ads.entity.AdCategoryAttribute;
import com.webrayan.bazaar.modules.ads.service.AdCategoryAttributeService;
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

class AdAdAdCategoryAttributeControllerTest {

    @InjectMocks
    private AdCategoryAttributeController adCategoryAttributeController;

    @Mock
    private AdCategoryAttributeService adCategoryAttributeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAttributes() {
        AdCategoryAttribute attribute1 = new AdCategoryAttribute();
        AdCategoryAttribute attribute2 = new AdCategoryAttribute();
        List<AdCategoryAttribute> attributes = Arrays.asList(attribute1, attribute2);

        when(adCategoryAttributeService.getAllAttributes()).thenReturn(attributes);

        List<AdCategoryAttribute> result = adCategoryAttributeController.getAllAttributes();

        assertEquals(2, result.size());
        verify(adCategoryAttributeService, times(1)).getAllAttributes();
    }

    @Test
    void testGetAttributeById_Found() {
        Long id = 1L;
        AdCategoryAttribute attribute = new AdCategoryAttribute();

        when(adCategoryAttributeService.getAttributeById(id)).thenReturn(Optional.of(attribute));

        ResponseEntity<AdCategoryAttribute> response = adCategoryAttributeController.getAttributeById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(adCategoryAttributeService, times(1)).getAttributeById(id);
    }

    @Test
    void testGetAttributeById_NotFound() {
        Long id = 1L;

        when(adCategoryAttributeService.getAttributeById(id)).thenReturn(Optional.empty());

        ResponseEntity<AdCategoryAttribute> response = adCategoryAttributeController.getAttributeById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(adCategoryAttributeService, times(1)).getAttributeById(id);
    }

    @Test
    void testCreateAttribute() {
        AdCategoryAttribute attribute = new AdCategoryAttribute();
        when(adCategoryAttributeService.saveAttribute(any(AdCategoryAttribute.class))).thenReturn(attribute);

        AdCategoryAttribute result = adCategoryAttributeController.createAttribute(attribute);

        assertNotNull(result);
        verify(adCategoryAttributeService, times(1)).saveAttribute(attribute);
    }

    @Test
    void testDeleteAttribute() {
        Long id = 1L;

        adCategoryAttributeController.deleteAttribute(id);

        verify(adCategoryAttributeService, times(1)).deleteAttribute(id);
    }
}
