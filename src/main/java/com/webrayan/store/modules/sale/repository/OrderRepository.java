package com.webrayan.store.modules.sale.repository;

import com.webrayan.store.modules.sale.entity.Order;
import com.webrayan.store.modules.sale.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    Page<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status, Pageable pageable);

    List<Order> findByStatusIn(List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByStatusAndDateRange(@Param("status") OrderStatus status,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.customer.email = :email ORDER BY o.orderDate DESC")
    List<Order> findByCustomerEmail(@Param("email") String email);

    @Query("SELECT o FROM Order o WHERE o.totalAmount >= :minAmount AND o.totalAmount <= :maxAmount")
    List<Order> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                       @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT o FROM Order o WHERE LOWER(o.deliveryCity) = LOWER(:city)")
    List<Order> findByDeliveryCity(@Param("city") String city);

    @Query("SELECT o FROM Order o WHERE o.couponCode = :couponCode")
    List<Order> findByCouponCode(@Param("couponCode") String couponCode);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    List<Order> findOrdersContainingProduct(@Param("productId") Long productId);

    // آمارهای مهم
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Long countOrdersByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.customer.id = :customerId AND o.status = 'DELIVERED'")
    BigDecimal sumTotalSpentByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :cutoffDate")
    List<Order> findExpiredPendingOrders(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Methods for admin panel with pagination and search
    
    /**
     * جستجو در سفارشات بر اساس شماره سفارش یا نام مشتری یا ایمیل
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE " +
           "o.orderNumber LIKE %:search% OR " +
           "o.customer.username LIKE %:search% OR " +
           "o.customer.email LIKE %:search% OR " +
           "o.customer.firstName LIKE %:search% OR " +
           "o.customer.lastName LIKE %:search%")
    Page<Order> findBySearchTerm(@Param("search") String search, Pageable pageable);

    /**
     * جستجو در سفارشات بر اساس وضعیت و عبارت جستجو
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status AND (" +
           "o.orderNumber LIKE %:search% OR " +
           "o.customer.username LIKE %:search% OR " +
           "o.customer.email LIKE %:search% OR " +
           "o.customer.firstName LIKE %:search% OR " +
           "o.customer.lastName LIKE %:search%)")
    Page<Order> findByStatusAndSearchTerm(@Param("status") OrderStatus status, 
                                         @Param("search") String search, 
                                         Pageable pageable);

    /**
     * جستجو در سفارشات بر اساس بازه تاریخ
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);

    /**
     * جستجو ترکیبی: وضعیت + تاریخ + جستجو
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderDate <= :endDate) AND " +
           "(:search IS NULL OR :search = '' OR " +
           "o.orderNumber LIKE %:search% OR " +
           "o.customer.username LIKE %:search% OR " +
           "o.customer.email LIKE %:search% OR " +
           "o.customer.firstName LIKE %:search% OR " +
           "o.customer.lastName LIKE %:search%)")
    Page<Order> findWithFilters(@Param("status") OrderStatus status,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               @Param("search") String search,
                               Pageable pageable);

    /**
     * دریافت همه سفارشات با customer eager loading
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer")
    Page<Order> findAllWithCustomer(Pageable pageable);
}
