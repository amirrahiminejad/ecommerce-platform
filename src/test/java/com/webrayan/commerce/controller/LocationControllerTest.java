package com.webrayan.commerce.controller;

import com.webrayan.commerce.core.common.controller.LocationController;
import com.webrayan.commerce.core.common.entity.Location;
import com.webrayan.commerce.core.common.service.LocationService;
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

class LocationControllerTest {

    @InjectMocks
    private LocationController locationController;

    @Mock
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLocations() {
        Location location1 = new Location();
        Location location2 = new Location();
        List<Location> locations = Arrays.asList(location1, location2);

        when(locationService.getAllLocations()).thenReturn(locations);

        List<Location> result = locationController.getAllLocations();

        assertEquals(2, result.size());
        verify(locationService, times(1)).getAllLocations();
    }

    @Test
    void testGetLocationById_Found() {
        Integer id = 1;
        Location location = new Location();

        when(locationService.getLocationById(id)).thenReturn(Optional.of(location));

        ResponseEntity<Location> response = locationController.getLocationById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(locationService, times(1)).getLocationById(id);
    }

    @Test
    void testGetLocationById_NotFound() {
        Integer id = 1;

        when(locationService.getLocationById(id)).thenReturn(Optional.empty());

        ResponseEntity<Location> response = locationController.getLocationById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(locationService, times(1)).getLocationById(id);
    }

    @Test
    void testCreateLocation() {
        Location location = new Location();
        when(locationService.createLocation(any(Location.class))).thenReturn(location);

        Location result = locationController.createLocation(location);

        assertNotNull(result);
        verify(locationService, times(1)).createLocation(location);
    }

    @Test
    void testUpdateLocation() {
        Integer id = 1;
        Location locationDetails = new Location();
        Location updatedLocation = new Location();

        when(locationService.updateLocation(eq(id), any(Location.class))).thenReturn(updatedLocation);

        ResponseEntity<Location> response = locationController.updateLocation(id, locationDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(locationService, times(1)).updateLocation(eq(id), any(Location.class));
    }

    @Test
    void testDeleteLocation() {
        Integer id = 1;

        locationController.deleteLocation(id);

        verify(locationService, times(1)).deleteLocation(id);
    }
}
