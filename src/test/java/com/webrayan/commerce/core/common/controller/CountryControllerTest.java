package com.webrayan.commerce.core.common.controller;

import com.webrayan.commerce.core.common.entity.Country;
import com.webrayan.commerce.core.common.service.CountryService;
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

class CountryControllerTest {

    @InjectMocks
    private CountryController countryController;

    @Mock
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCountries() {
        Country country1 = new Country();
        Country country2 = new Country();
        List<Country> countries = Arrays.asList(country1, country2);

        when(countryService.getAllCountries()).thenReturn(countries);

        List<Country> result = countryController.getAllCountries();

        assertEquals(2, result.size());
        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    void testGetCountryById_Found() {
        Integer id = 1;
        Country country = new Country();

        when(countryService.getCountryById(id)).thenReturn(Optional.of(country));

        ResponseEntity<Country> response = countryController.getCountryById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(countryService, times(1)).getCountryById(id);
    }

    @Test
    void testGetCountryById_NotFound() {
        Integer id = 1;

        when(countryService.getCountryById(id)).thenReturn(Optional.empty());

        ResponseEntity<Country> response = countryController.getCountryById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(countryService, times(1)).getCountryById(id);
    }

    @Test
    void testCreateCountry() {
        Country country = new Country();
        when(countryService.createCountry(any(Country.class))).thenReturn(country);

        Country result = countryController.createCountry(country);

        assertNotNull(result);
        verify(countryService, times(1)).createCountry(country);
    }

    @Test
    void testUpdateCountry() {
        Integer id = 1;
        Country countryDetails = new Country();
        Country updatedCountry = new Country();

        when(countryService.updateCountry(eq(id), any(Country.class))).thenReturn(updatedCountry);

        ResponseEntity<Country> response = countryController.updateCountry(id, countryDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(countryService, times(1)).updateCountry(eq(id), any(Country.class));
    }

    @Test
    void testDeleteCountry() {
        Integer id = 1;

        countryController.deleteCountry(id);

        verify(countryService, times(1)).deleteCountry(id);
    }
}
