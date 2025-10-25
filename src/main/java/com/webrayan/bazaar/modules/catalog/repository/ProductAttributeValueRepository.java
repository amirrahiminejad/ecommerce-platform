package com.webrayan.bazaar.modules.catalog.repository;

import com.webrayan.bazaar.modules.catalog.entity.ProductAttributeValue;
import com.webrayan.bazaar.modules.catalog.entity.Product;
import com.webrayan.bazaar.modules.catalog.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    
    List<ProductAttributeValue> findByProduct(Product product);
    
    List<ProductAttributeValue> findByProductId(Long productId);
    
    Optional<ProductAttributeValue> findByProductAndAttribute(Product product, CategoryAttribute attribute);
    
    @Query("SELECT pav FROM ProductAttributeValue pav WHERE pav.product = :product ORDER BY pav.attribute.sortOrder ASC")
    List<ProductAttributeValue> findByProductOrderByAttributeSort(@Param("product") Product product);
    
    @Query("SELECT pav FROM ProductAttributeValue pav WHERE pav.attribute = :attribute AND pav.value = :value")
    List<ProductAttributeValue> findByAttributeAndValue(@Param("attribute") CategoryAttribute attribute, @Param("value") String value);
    
    void deleteByProductAndAttribute(Product product, CategoryAttribute attribute);
    
    void deleteByProduct(Product product);
}
