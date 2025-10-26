package com.webrayan.commerce.modules.catalog.repository;

import com.webrayan.commerce.modules.catalog.entity.Product;
import com.webrayan.commerce.modules.catalog.entity.Category;
import com.webrayan.commerce.modules.catalog.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    boolean existsBySku(String sku);
    
    List<Product> findByCategoryAndIsActiveTrueAndStatus(Category category, ProductStatus status);
    
    Page<Product> findByIsActiveTrueAndStatus(ProductStatus status, Pageable pageable);
    
    Page<Product> findByCategoryAndIsActiveTrueAndStatus(Category category, ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.status = :status AND p.isFeatured = true")
    List<Product> findFeaturedProducts(@Param("status") ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.status = :status AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("status") ProductStatus status, 
                                   @Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice, 
                                   Pageable pageable);
    
//    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.status = :status AND " +
//           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//           "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<Product> searchProducts(@Param("keyword") String keyword,
//                                 @Param("status") ProductStatus status,
//                                 Pageable pageable);
//
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.status = :status AND p.manageStock = true AND p.stockQuantity <= p.minStockLevel")
    List<Product> findLowStockProducts(@Param("status") ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.status = :status AND p.manageStock = true AND p.stockQuantity = 0")
    List<Product> findOutOfStockProducts(@Param("status") ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.seller.id = :userId AND p.isActive = true")
    Page<Product> findBySeller(@Param("userId") Long userId, Pageable pageable);
    
    // متدهای جدید برای Admin Panel
    
    @Query("SELECT p FROM Product p WHERE p.status = :status")
    Page<Product> findByStatus(@Param("status") ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = :isActive")
    Page<Product> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isFeatured = :isFeatured")
    Page<Product> findByIsFeatured(@Param("isFeatured") Boolean isFeatured, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findByNameContainingOrSkuContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND p.status = :status")
    Page<Product> findByNameContainingOrSkuContainingAndStatus(@Param("search") String search, 
                                                               @Param("status") ProductStatus status, 
                                                               Pageable pageable);
    
    // آمار محصولات
    
    Long countByStatus(ProductStatus status);
    
    Long countByIsActive(Boolean isActive);
    
    Long countByIsFeatured(Boolean isFeatured);
}
