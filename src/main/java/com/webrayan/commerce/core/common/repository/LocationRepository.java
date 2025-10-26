package com.webrayan.commerce.core.common.repository;

import com.webrayan.commerce.core.common.entity.Country;
import com.webrayan.commerce.core.common.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    boolean existsByCityAndCountry(String city, Country country);
}
