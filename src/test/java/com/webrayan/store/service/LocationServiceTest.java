package com.webrayan.store.service;

import com.webrayan.store.core.common.entity.Country;
import com.webrayan.store.core.common.entity.Location;
import com.webrayan.store.core.common.repository.LocationRepository;
import com.webrayan.store.core.common.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationServiceTest {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLocations_ShouldReturnAllLocations() {
        // Arrange
        Location location1 = new Location();
        location1.setCity("Tehran");
        Location location2 = new Location();
        location2.setCity("Mashhad");

        when(locationRepository.findAll()).thenReturn(List.of(location1, location2));

        // Act
        List<Location> locations = locationService.getAllLocations();

        // Assert
        assertEquals(2, locations.size());
        assertEquals("Tehran", locations.get(0).getCity());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void getLocationById_ShouldReturnLocation_WhenExists() {
        // Arrange
        Country country = new Country("Iran", "IR");
        Location location = new Location();
        location.setId(1);
        location.setCity("Tehran");
        location.setCountry(country);

        when(locationRepository.findById(1)).thenReturn(Optional.of(location));

        // Act
        Optional<Location> result = locationService.getLocationById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Tehran", result.get().getCity());
        assertEquals("Iran", result.get().getCountry().getName());
        verify(locationRepository, times(1)).findById(1);
    }

    @Test
    void createLocation_ShouldSaveAndReturnLocation() {
        // Arrange
        Country country = new Country("Iran", "IR");
        Location location = new Location();
        location.setCity("Isfahan");
        location.setCountry(country);

        when(locationRepository.save(location)).thenReturn(location);

        // Act
        Location result = locationService.createLocation(location);

        // Assert
        assertNotNull(result);
        assertEquals("Isfahan", result.getCity());
        verify(locationRepository, times(1)).save(location);
    }

    @Test
    void updateLocation_ShouldUpdateAndReturnUpdatedLocation() {
        // Arrange
        Location existingLocation = new Location();
        existingLocation.setId(1);
        existingLocation.setCity("Tehran");

        Country newCountry = new Country("USA", "US");
        Location updatedDetails = new Location();
        updatedDetails.setCity("Los Angeles");
        updatedDetails.setCountry(newCountry);

        when(locationRepository.findById(1)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(existingLocation)).thenAnswer(invocation -> {
            Location updatedLocation = invocation.getArgument(0);
            updatedLocation.setId(1);
            return updatedLocation;
        });

        // Act
        Location result = locationService.updateLocation(1, updatedDetails);

        // Assert
        assertEquals("Los Angeles", result.getCity());
        assertEquals("USA", result.getCountry().getName());
        verify(locationRepository, times(1)).findById(1);
        verify(locationRepository, times(1)).save(existingLocation);
    }

    @Test
    void deleteLocation_ShouldCallDeleteById() {
        // Arrange
        int id = 1;
        doNothing().when(locationRepository).deleteById(id);

        // Act
        locationService.deleteLocation(id);

        // Assert
        verify(locationRepository, times(1)).deleteById(id);
    }
}
