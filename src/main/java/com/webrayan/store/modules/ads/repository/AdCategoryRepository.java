package com.webrayan.store.modules.ads.repository;

import com.webrayan.store.modules.ads.entity.AdCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdCategoryRepository extends JpaRepository<AdCategory, Long> {
    Optional<AdCategory> findByName(String name);

//    List<Category> findByParentCategory(Category parentCategory);

    boolean existsByName(String name);
}
