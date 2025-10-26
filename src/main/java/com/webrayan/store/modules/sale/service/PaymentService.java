package com.webrayan.store.modules.sale.service;

import com.webrayan.store.modules.sale.entity.Payment;
import com.webrayan.store.modules.sale.entity.Order;
import com.webrayan.store.modules.sale.enums.OrderStatus;
import com.webrayan.store.modules.sale.enums.PaymentStatus;
import com.webrayan.store.modules.sale.enums.PaymentMethod;
import com.webrayan.store.modules.sale.repository.PaymentRepository;
import com.webrayan.store.modules.sale.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPayment(Long orderId, PaymentMethod paymentMethod, BigDecimal amount, 
                               String gatewayName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // بررسی مبلغ پرداخت
        if (amount.compareTo(order.getTotalAmount()) > 0) {
            throw new RuntimeException("Payment amount cannot exceed order total");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentReference(generatePaymentReference());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(amount);
        payment.setCurrency(order.getCurrency());
        payment.setGatewayName(gatewayName);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processSuccessfulPayment(String paymentReference, String gatewayTransactionId, 
                                          String gatewayResponse) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in pending status");
        }

        payment.markAsPaid(gatewayTransactionId);
        payment.setGatewayResponse(gatewayResponse);

        Payment savedPayment = paymentRepository.save(payment);

        // به‌روزرسانی وضعیت سفارش در صورت پرداخت کامل
        Order order = payment.getOrder();
        BigDecimal totalPaid = order.getTotalPaid();
        if (totalPaid.compareTo(order.getTotalAmount()) >= 0) {
            // سفارش کاملاً پرداخت شده
            if (order.getStatus() == OrderStatus.PENDING) {
                order.updateStatus(OrderStatus.CONFIRMED, "Payment completed");
                orderRepository.save(order);
            }
        }

        return savedPayment;
    }

    @Transactional
    public Payment processFailedPayment(String paymentReference, String failureReason) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in pending status");
        }

        payment.markAsFailed(failureReason);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processRefund(Long paymentId, BigDecimal refundAmount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Only paid payments can be refunded");
        }

        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed payment amount");
        }

        payment.processRefund(refundAmount, reason);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Optional<Payment> findByPaymentReference(String paymentReference) {
        return paymentRepository.findByPaymentReference(paymentReference);
    }

    public Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId) {
        return paymentRepository.findByGatewayTransactionId(gatewayTransactionId);
    }

    public List<Payment> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    public BigDecimal getTotalPaidByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.sumAmountByStatusAndDateRange(PaymentStatus.PAID, startDate, endDate);
    }

    public Long getPaymentCountByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    public List<Object[]> getPaymentMethodStatistics() {
        return paymentRepository.getPaymentMethodStatistics();
    }

    public BigDecimal getCustomerTotalPaid(Long customerId) {
        return paymentRepository.getTotalPaidByCustomer(customerId);
    }

    public BigDecimal getTotalRefundedAmount() {
        return paymentRepository.getTotalRefundedAmount();
    }

    @Transactional
    public void processExpiredPendingPayments() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(1); // 1 ساعت
        List<Payment> expiredPayments = paymentRepository.findExpiredPendingPayments(cutoffDate);
        
        for (Payment payment : expiredPayments) {
            processFailedPayment(payment.getPaymentReference(), "Payment expired");
        }
    }

    public List<Payment> getPaymentsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentRepository.findSuccessfulPaymentsByAmountRange(minAmount, maxAmount);
    }

    public boolean isOrderFullyPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        BigDecimal totalPaid = order.getTotalPaid();
        return totalPaid.compareTo(order.getTotalAmount()) >= 0;
    }

    public BigDecimal getOrderRemainingBalance(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        BigDecimal totalPaid = order.getTotalPaid();
        return order.getTotalAmount().subtract(totalPaid);
    }

    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Payment createCashOnDeliveryPayment(Long orderId) {
        return createPayment(orderId, PaymentMethod.CASH_ON_DELIVERY, 
                           orderRepository.findById(orderId).get().getTotalAmount(), "COD");
    }

    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus(PaymentStatus.PENDING);
    }

    public List<Payment> getFailedPayments() {
        return paymentRepository.findByStatus(PaymentStatus.FAILED);
    }
}
