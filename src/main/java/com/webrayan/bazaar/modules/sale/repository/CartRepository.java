package com.webrayan.bazaar.modules.sale.repository;

import com.webrayan.bazaar.modules.sale.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId ORDER BY ci.createdAt DESC")
    List<CartItem> findCartItemsByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(ci.quantity * ci.unitPrice) FROM CartItem ci WHERE ci.user.id = :userId")
    BigDecimal getCartTotalByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.user.id = :userId")
    Long getCartItemCountByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.user.id = :userId")
    Long getTotalQuantityByUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    void clearCartByUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
    void removeItemFromCart(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.createdAt < :cutoffDate")
    List<CartItem> findOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.createdAt < :cutoffDate")
    void deleteOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT ci.product.id, SUM(ci.quantity) FROM CartItem ci GROUP BY ci.product.id ORDER BY SUM(ci.quantity) DESC")
    List<Object[]> getMostAddedToCartProducts();

    @Query("SELECT DISTINCT ci.user.id FROM CartItem ci WHERE ci.product.id = :productId")
    List<Long> getUsersWithProductInCart(@Param("productId") Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
