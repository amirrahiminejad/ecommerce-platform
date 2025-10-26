package com.webrayan.commerce.modules.sale.repository;

import com.webrayan.commerce.modules.sale.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findItemsByOrder(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId ORDER BY oi.order.orderDate DESC")
    List<OrderItem> findByProductOrderByOrderDateDesc(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.status = 'DELIVERED'")
    Long getTotalSoldQuantityForProduct(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.status = 'DELIVERED'")
    BigDecimal getTotalRevenueForProduct(@Param("productId") Long productId);

    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
           "WHERE oi.order.status = 'DELIVERED' GROUP BY oi.product.id ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts(@Param("limit") int limit);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.customer.id = :customerId")
    List<OrderItem> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT AVG(oi.unitPrice) FROM OrderItem oi WHERE oi.product.id = :productId")
    BigDecimal getAveragePriceForProduct(@Param("productId") Long productId);

    @Query("SELECT COUNT(DISTINCT oi.order.customer.id) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long getUniqueCustomersForProduct(@Param("productId") Long productId);
}
