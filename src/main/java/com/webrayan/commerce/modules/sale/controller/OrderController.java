package com.webrayan.commerce.modules.sale.controller;

import com.webrayan.commerce.modules.sale.service.OrderService;
import com.webrayan.commerce.modules.sale.entity.Order;
import com.webrayan.commerce.modules.sale.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController("saleOrderController")
@RequestMapping("/api/sale/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-from-cart")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'SYSTEM_ADMIN') and #customerId == authentication.principal.id")
    public ResponseEntity<Order> createOrderFromCart(
            @RequestParam Long customerId,
            @RequestParam String deliveryAddress,
            @RequestParam String deliveryCity,
            @RequestParam String deliveryState,
            @RequestParam String deliveryPostalCode,
            @RequestParam String deliveryPhone,
            @RequestParam String deliveryName,
            @RequestParam(required = false) String customerNotes) {
        
        Order order = orderService.createOrderFromCart(customerId, deliveryAddress, deliveryCity, 
                deliveryState, deliveryPostalCode, deliveryPhone, deliveryName, customerNotes);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or @orderService.isOrderOwner(#id, authentication.name)")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or @orderService.isOrderOwnerByNumber(#orderNumber, authentication.name)")
    public ResponseEntity<Order> getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.findByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or #customerId == authentication.principal.id")
    public ResponseEntity<Page<Order>> getOrdersByCustomer(
            @PathVariable Long customerId, 
            Pageable pageable) {
        Page<Order> orders = orderService.getOrdersByCustomer(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Order>> getOrdersByStatus(
            @PathVariable OrderStatus status, 
            Pageable pageable) {
        Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}/recent")
    public ResponseEntity<List<Order>> getRecentOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Order> orders = orderService.getRecentOrdersByCustomer(customerId, limit);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long id) {
        Order order = orderService.confirmOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<Order> processOrder(@PathVariable Long id) {
        Order order = orderService.processOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<Order> shipOrder(@PathVariable Long id) {
        Order order = orderService.shipOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<Order> deliverOrder(@PathVariable Long id) {
        Order order = orderService.deliverOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id, @RequestParam String reason) {
        Order order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/address")
    public ResponseEntity<Order> updateOrderAddress(
            @PathVariable Long id,
            @RequestParam String deliveryAddress,
            @RequestParam String deliveryCity,
            @RequestParam String deliveryState,
            @RequestParam String deliveryPostalCode,
            @RequestParam String deliveryPhone,
            @RequestParam String deliveryName) {
        
        Order order = orderService.updateOrderAddress(id, deliveryAddress, deliveryCity, 
                deliveryState, deliveryPostalCode, deliveryPhone, deliveryName);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(@RequestParam String searchTerm) {
        List<Order> orders = orderService.searchOrders(searchTerm);
        return ResponseEntity.ok(orders);
    }

    // آمارها و گزارشات
    @GetMapping("/statistics/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal revenue = orderService.getTotalRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/statistics/count")
    public ResponseEntity<Map<OrderStatus, Long>> getOrderCountByStatus() {
        Map<OrderStatus, Long> statistics = Map.of(
                OrderStatus.PENDING, orderService.getOrderCountByStatus(OrderStatus.PENDING),
                OrderStatus.CONFIRMED, orderService.getOrderCountByStatus(OrderStatus.CONFIRMED),
                OrderStatus.PROCESSING, orderService.getOrderCountByStatus(OrderStatus.PROCESSING),
                OrderStatus.SHIPPED, orderService.getOrderCountByStatus(OrderStatus.SHIPPED),
                OrderStatus.DELIVERED, orderService.getOrderCountByStatus(OrderStatus.DELIVERED),
                OrderStatus.CANCELLED, orderService.getOrderCountByStatus(OrderStatus.CANCELLED)
        );
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/customer/{customerId}/total-spent")
    public ResponseEntity<BigDecimal> getCustomerTotalSpent(@PathVariable Long customerId) {
        BigDecimal totalSpent = orderService.getCustomerTotalSpent(customerId);
        return ResponseEntity.ok(totalSpent != null ? totalSpent : BigDecimal.ZERO);
    }

    @PostMapping("/cleanup/expired")
    public ResponseEntity<String> processExpiredOrders() {
        orderService.processExpiredPendingOrders();
        return ResponseEntity.ok("Expired pending orders processed successfully");
    }
}
