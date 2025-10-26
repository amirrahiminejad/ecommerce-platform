package com.webrayan.commerce.core.common.service;

import com.webrayan.commerce.core.common.entity.Location;
import com.webrayan.commerce.core.common.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Integer id) {
        return locationRepository.findById(id);
    }
    
    public Location getLocationById(Long id) {
        return locationRepository.findById(id.intValue()).orElse(null);
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location updateLocation(Integer id, Location locationDetails) {
        Location location = locationRepository.findById(id).orElseThrow();
        location.setCity(locationDetails.getCity());
        location.setCountry(locationDetails.getCountry());
        return locationRepository.save(location);
    }

    public void deleteLocation(Integer id) {
        locationRepository.deleteById(id);
    }
}
