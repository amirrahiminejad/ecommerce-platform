package com.webrayan.store.modules.sale.repository;

import com.webrayan.store.modules.sale.entity.Payment;
import com.webrayan.store.modules.sale.enums.PaymentMethod;
import com.webrayan.store.modules.sale.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.order.customer.id = :customerId ORDER BY p.paymentDate DESC")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByStatusAndDateRange(@Param("status") PaymentStatus status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodStatistics();

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffDate")
    List<Payment> findExpiredPendingPayments(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT p FROM Payment p WHERE p.gatewayName = :gatewayName")
    List<Payment> findByGatewayName(@Param("gatewayName") String gatewayName);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.order.customer.id = :customerId AND p.status = 'PAID'")
    BigDecimal getTotalPaidByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.status IN ('REFUNDED', 'PARTIAL_REFUNDED')")
    BigDecimal getTotalRefundedAmount();

    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount AND p.amount <= :maxAmount AND p.status = 'PAID'")
    List<Payment> findSuccessfulPaymentsByAmountRange(@Param("minAmount") BigDecimal minAmount, 
                                                     @Param("maxAmount") BigDecimal maxAmount);
}
