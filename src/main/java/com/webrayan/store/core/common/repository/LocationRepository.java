package com.webrayan.store.core.common.repository;

import com.webrayan.store.core.common.entity.Country;
import com.webrayan.store.core.common.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    boolean existsByCityAndCountry(String city, Country country);
}
