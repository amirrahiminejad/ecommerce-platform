package com.webrayan.store.modules.catalog.repository;

import com.webrayan.store.modules.catalog.entity.ProductImage;
import com.webrayan.store.modules.catalog.entity.Product;
import com.webrayan.store.core.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductOrderBySortOrderAsc(Product product);
    
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
    
    Optional<ProductImage> findByProductAndIsPrimaryTrue(Product product);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product = :product AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProduct(@Param("product") Product product);
    
    @Transactional
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product = :product")
    void resetPrimaryImagesForProduct(@Param("product") Product product);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product = :product AND pi.image = :image")
    void deleteByProductAndImage(Product product, Image image);
    
    void deleteByProduct(Product product);
    
    Long countByProduct(Product product);
}
