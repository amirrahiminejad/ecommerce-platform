package com.webrayan.bazaar.core.common.repository;

import com.webrayan.bazaar.core.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    boolean existsByCode(String code);
    Country findByCode(String code);
}
