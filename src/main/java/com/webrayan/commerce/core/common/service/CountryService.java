package com.webrayan.commerce.core.common.service;

import com.webrayan.commerce.core.common.repository.CountryRepository;
import com.webrayan.commerce.core.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Optional<Country> getCountryById(Integer id) {
        return countryRepository.findById(id);
    }

    public Country createCountry(Country country) {
        return countryRepository.save(country);
    }

    public Country updateCountry(Integer id, Country countryDetails) {
        Country country = countryRepository.findById(id).orElseThrow();
        country.setName(countryDetails.getName());
        country.setCode(countryDetails.getCode());
        return countryRepository.save(country);
    }

    public void deleteCountry(Integer id) {
        countryRepository.deleteById(id);
    }
}
