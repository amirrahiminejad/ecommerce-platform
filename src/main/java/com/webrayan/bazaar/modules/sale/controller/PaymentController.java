package com.webrayan.bazaar.modules.sale.controller;

import com.webrayan.bazaar.modules.sale.service.PaymentService;
import com.webrayan.bazaar.modules.sale.entity.Payment;
import com.webrayan.bazaar.modules.sale.enums.PaymentMethod;
import com.webrayan.bazaar.modules.sale.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long orderId,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam BigDecimal amount,
            @RequestParam String gatewayName) {
        
        Payment payment = paymentService.createPayment(orderId, paymentMethod, amount, gatewayName);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/create-cod")
    public ResponseEntity<Payment> createCashOnDeliveryPayment(@RequestParam Long orderId) {
        Payment payment = paymentService.createCashOnDeliveryPayment(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<Payment> getPaymentByReference(@PathVariable String paymentReference) {
        return paymentService.findByPaymentReference(paymentReference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transaction/{gatewayTransactionId}")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String gatewayTransactionId) {
        return paymentService.findByGatewayTransactionId(gatewayTransactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(@PathVariable Long orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getPaymentsByCustomer(@PathVariable Long customerId) {
        List<Payment> payments = paymentService.getPaymentsByCustomer(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        List<Payment> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<Payment>> getFailedPayments() {
        List<Payment> payments = paymentService.getFailedPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/success")
    public ResponseEntity<Payment> processSuccessfulPayment(
            @RequestParam String paymentReference,
            @RequestParam String gatewayTransactionId,
            @RequestParam(required = false) String gatewayResponse) {
        
        Payment payment = paymentService.processSuccessfulPayment(
                paymentReference, gatewayTransactionId, gatewayResponse);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/failure")
    public ResponseEntity<Payment> processFailedPayment(
            @RequestParam String paymentReference,
            @RequestParam String failureReason) {
        
        Payment payment = paymentService.processFailedPayment(paymentReference, failureReason);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> processRefund(
            @PathVariable Long id,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {
        
        Payment payment = paymentService.processRefund(id, refundAmount, reason);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}/fully-paid")
    public ResponseEntity<Boolean> isOrderFullyPaid(@PathVariable Long orderId) {
        boolean fullyPaid = paymentService.isOrderFullyPaid(orderId);
        return ResponseEntity.ok(fullyPaid);
    }

    @GetMapping("/order/{orderId}/remaining-balance")
    public ResponseEntity<BigDecimal> getOrderRemainingBalance(@PathVariable Long orderId) {
        BigDecimal balance = paymentService.getOrderRemainingBalance(orderId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/amount-range")
    public ResponseEntity<List<Payment>> getPaymentsByAmountRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        List<Payment> payments = paymentService.getPaymentsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(payments);
    }

    // آمارها و گزارشات
    @GetMapping("/statistics/total-paid")
    public ResponseEntity<BigDecimal> getTotalPaid(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal totalPaid = paymentService.getTotalPaidByDateRange(startDate, endDate);
        return ResponseEntity.ok(totalPaid != null ? totalPaid : BigDecimal.ZERO);
    }

    @GetMapping("/statistics/count")
    public ResponseEntity<Map<PaymentStatus, Long>> getPaymentCountByStatus() {
        Map<PaymentStatus, Long> statistics = Map.of(
                PaymentStatus.PENDING, paymentService.getPaymentCountByStatus(PaymentStatus.PENDING),
                PaymentStatus.PAID, paymentService.getPaymentCountByStatus(PaymentStatus.PAID),
                PaymentStatus.FAILED, paymentService.getPaymentCountByStatus(PaymentStatus.FAILED),
                PaymentStatus.CANCELLED, paymentService.getPaymentCountByStatus(PaymentStatus.CANCELLED),
                PaymentStatus.REFUNDED, paymentService.getPaymentCountByStatus(PaymentStatus.REFUNDED)
        );
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/payment-methods")
    public ResponseEntity<List<Object[]>> getPaymentMethodStatistics() {
        List<Object[]> statistics = paymentService.getPaymentMethodStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/customer/{customerId}/total-paid")
    public ResponseEntity<BigDecimal> getCustomerTotalPaid(@PathVariable Long customerId) {
        BigDecimal totalPaid = paymentService.getCustomerTotalPaid(customerId);
        return ResponseEntity.ok(totalPaid != null ? totalPaid : BigDecimal.ZERO);
    }

    @GetMapping("/statistics/total-refunded")
    public ResponseEntity<BigDecimal> getTotalRefundedAmount() {
        BigDecimal totalRefunded = paymentService.getTotalRefundedAmount();
        return ResponseEntity.ok(totalRefunded != null ? totalRefunded : BigDecimal.ZERO);
    }

    @PostMapping("/cleanup/expired")
    public ResponseEntity<String> processExpiredPayments() {
        paymentService.processExpiredPendingPayments();
        return ResponseEntity.ok("Expired pending payments processed successfully");
    }
}
