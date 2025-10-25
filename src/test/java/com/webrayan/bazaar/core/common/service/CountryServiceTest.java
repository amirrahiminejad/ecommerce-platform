package com.webrayan.bazaar.service;

import com.webrayan.bazaar.core.common.entity.Country;
import com.webrayan.bazaar.core.common.entity.Location;
import com.webrayan.bazaar.core.common.repository.CountryRepository;
import com.webrayan.bazaar.core.common.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCountries_ShouldReturnAllCountries() {
        // Arrange
        Country country1 = new Country("Iran", "IR");
        Country country2 = new Country("USA", "US");
        when(countryRepository.findAll()).thenReturn(List.of(country1, country2));

        // Act
        List<Country> countries = countryService.getAllCountries();

        // Assert
        assertEquals(2, countries.size());
        assertEquals("Iran", countries.get(0).getName());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void getCountryById_ShouldReturnCountryWithCities_WhenExists() {
        // Arrange
        Location city1 = new Location();
        city1.setCity("Tehran");
        Location city2 = new Location();
        city2.setCity("Mashhad");

        Country mockCountry = new Country("Iran", "IR");
        mockCountry.setCities(Set.of(city1, city2));
        when(countryRepository.findById(1)).thenReturn(Optional.of(mockCountry));

        // Act
        Optional<Country> country = countryService.getCountryById(1);

        // Assert
        assertTrue(country.isPresent());
        assertEquals("Iran", country.get().getName());
        assertEquals(2, country.get().getCities().size());
        verify(countryRepository, times(1)).findById(1);
    }

    @Test
    void createCountry_ShouldSaveAndReturnCountry() {
        // Arrange
        Country mockCountry = new Country("Canada", "CA");
        Country savedCountry = new Country("Canada", "CA");
        savedCountry.setId(3);
        when(countryRepository.save(mockCountry)).thenReturn(savedCountry);

        // Act
        Country result = countryService.createCountry(mockCountry);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Canada", result.getName());
        verify(countryRepository, times(1)).save(mockCountry);
    }

    @Test
    void updateCountry_ShouldUpdateAndReturnUpdatedCountry() {
        // Arrange
        Country existingCountry = new Country("Iran", "IR");
        existingCountry.setId(1);

        Country updatedDetails = new Country("Persia", "PE");

        when(countryRepository.findById(1)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.save(existingCountry)).thenAnswer(invocation -> {
            Country updatedCountry = invocation.getArgument(0);
            updatedCountry.setId(1);
            return updatedCountry;
        });

        // Act
        Country result = countryService.updateCountry(1, updatedDetails);

        // Assert
        assertEquals("Persia", result.getName());
        assertEquals("PE", result.getCode());
        verify(countryRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).save(existingCountry);
    }

    @Test
    void deleteCountry_ShouldCallDeleteById() {
        // Arrange
        int id = 1;
        doNothing().when(countryRepository).deleteById(id);

        // Act
        countryService.deleteCountry(id);

        // Assert
        verify(countryRepository, times(1)).deleteById(id);
    }
}
