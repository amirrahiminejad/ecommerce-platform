package com.webrayan.store.modules.catalog.repository;

import com.webrayan.store.modules.catalog.entity.ProductImageVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageVariantRepository extends JpaRepository<ProductImageVariant, Long> {

    /**
     * Find all variants for a product image
     */
    List<ProductImageVariant> findByProductImageId(Long productImageId);

    /**
     * Find specific variant by product image and size name
     */
    Optional<ProductImageVariant> findByProductImageIdAndSizeName(Long productImageId, String sizeName);

    /**
     * Delete all variants for a product image
     */
    void deleteByProductImageId(Long productImageId);

    /**
     * Find variants by file reference
     */
    Optional<ProductImageVariant> findByFileReference(String fileReference);

    /**
     * Get all variants for a product
     */
    @Query("SELECT piv FROM ProductImageVariant piv JOIN piv.productImage pi WHERE pi.product.id = :productId")
    List<ProductImageVariant> findByProductId(@Param("productId") Long productId);
}
