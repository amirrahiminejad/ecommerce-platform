package com.webrayan.bazaar.modules.catalog.repository;

import com.webrayan.bazaar.modules.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    boolean existsByName(String name);
    
    Category findByName(String name);
    
    boolean existsByNameAndParent(String name, Category parent);
    
    List<Category> findByParentIsNullAndIsActiveTrue();
    
    List<Category> findByParentIdAndIsActiveTrue(Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.parent = :parent AND c.isActive = true ORDER BY c.sortOrder ASC")
    List<Category> findActiveSubcategories(@Param("parent") Category parent);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder ASC")
    List<Category> findActiveRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.sortOrder ASC")
    List<Category> findAllActiveCategories();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category AND p.isActive = true")
    Long countActiveProductsByCategory(@Param("category") Category category);
}
