package com.webrayan.store.modules.catalog.repository;

import com.webrayan.store.modules.catalog.entity.CategoryAttribute;
import com.webrayan.store.modules.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {
    
    List<CategoryAttribute> findByCategoryAndIsActiveTrueOrderBySortOrderAsc(Category category);
    
    List<CategoryAttribute> findByCategoryIdAndIsActiveTrueOrderBySortOrderAsc(Long categoryId);
    
    @Query("SELECT ca FROM CategoryAttribute ca WHERE ca.category = :category AND ca.isActive = true AND ca.isRequired = true ORDER BY ca.sortOrder ASC")
    List<CategoryAttribute> findRequiredAttributesByCategory(@Param("category") Category category);
    
    @Query("SELECT ca FROM CategoryAttribute ca WHERE ca.category = :category AND ca.isActive = true AND ca.isFilterable = true ORDER BY ca.sortOrder ASC")
    List<CategoryAttribute> findFilterableAttributesByCategory(@Param("category") Category category);
    
    @Query("SELECT ca FROM CategoryAttribute ca WHERE ca.category = :category AND ca.isActive = true AND ca.isSearchable = true ORDER BY ca.sortOrder ASC")
    List<CategoryAttribute> findSearchableAttributesByCategory(@Param("category") Category category);
    
    boolean existsByCategoryAndName(Category category, String name);
}
