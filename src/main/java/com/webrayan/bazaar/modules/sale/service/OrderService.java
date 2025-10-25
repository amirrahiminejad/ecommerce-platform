package com.webrayan.bazaar.modules.sale.service;

import com.webrayan.bazaar.modules.sale.entity.Order;
import com.webrayan.bazaar.modules.sale.entity.OrderItem;
import com.webrayan.bazaar.modules.sale.entity.CartItem;
import com.webrayan.bazaar.modules.sale.enums.OrderStatus;
import com.webrayan.bazaar.modules.sale.repository.OrderRepository;
import com.webrayan.bazaar.modules.sale.repository.CartRepository;
import com.webrayan.bazaar.modules.catalog.entity.Product;
import com.webrayan.bazaar.modules.catalog.repository.ProductRepository;
import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.modules.acl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrderFromCart(Long userId, String deliveryAddress, String deliveryCity, 
                                   String deliveryState, String deliveryPostalCode, 
                                   String deliveryPhone, String deliveryName, 
                                   String customerNotes) {
        
        // دریافت آیتم‌های سبد خرید
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // دریافت کاربر
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ایجاد سفارش
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setCurrency("$");
        
        // تنظیم آدرس تحویل
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryCity(deliveryCity);
        order.setDeliveryState(deliveryState);
        order.setDeliveryPostalCode(deliveryPostalCode);
        order.setDeliveryPhone(deliveryPhone);
        order.setDeliveryName(deliveryName);
        order.setCustomerNotes(customerNotes);

        // تبدیل آیتم‌های سبد به آیتم‌های سفارش
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            // بررسی موجودی
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setProductDescription(product.getDescription());
            orderItem.setProductAttributes(cartItem.getSelectedAttributes());

            order.addOrderItem(orderItem);
            subtotal = subtotal.add(cartItem.getTotalPrice());

            // کاهش موجودی محصول
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setSubtotal(subtotal);
        order.setTotalAmount(order.calculateTotal());

        // ذخیره سفارش
        Order savedOrder = orderRepository.save(order);

        // پاک کردن سبد خرید
        cartRepository.clearCartByUser(userId);

        return savedOrder;
    }

    @Transactional
    public Order confirmOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be confirmed");
        }
        
        order.updateStatus(OrderStatus.CONFIRMED, "Order confirmed by admin");
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        // بازگردانی موجودی محصولات
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.updateStatus(OrderStatus.CANCELLED, reason);
        return orderRepository.save(order);
    }

    @Transactional
    public Order processOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed orders can be processed");
        }
        
        order.updateStatus(OrderStatus.PROCESSING, "Order processing started");
        return orderRepository.save(order);
    }

    @Transactional
    public Order shipOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException("Only processing orders can be shipped");
        }
        
        order.updateStatus(OrderStatus.SHIPPED, "Order shipped");
        return orderRepository.save(order);
    }

    @Transactional
    public Order deliverOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Only shipped orders can be delivered");
        }
        
        order.updateStatus(OrderStatus.DELIVERED, "Order delivered successfully");
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public Page<Order> getOrdersByCustomer(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    public List<Order> getRecentOrdersByCustomer(Long customerId, int limit) {
        return orderRepository.findRecentOrdersByCustomer(customerId, 
                Pageable.ofSize(limit));
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }

    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.sumTotalAmountByStatusAndDateRange(
                OrderStatus.DELIVERED, startDate, endDate);
    }

    public Long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    public BigDecimal getCustomerTotalSpent(Long customerId) {
        return orderRepository.sumTotalSpentByCustomer(customerId);
    }

    @Transactional
    public void processExpiredPendingOrders() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24); // 24 ساعت
        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(cutoffDate);
        
        for (Order order : expiredOrders) {
            cancelOrder(order.getId(), "Order expired - not confirmed within 24 hours");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Order updateOrderAddress(Long orderId, String deliveryAddress, String deliveryCity, 
                                  String deliveryState, String deliveryPostalCode, 
                                  String deliveryPhone, String deliveryName) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cannot update address for order with status: " + order.getStatus());
        }

        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryCity(deliveryCity);
        order.setDeliveryState(deliveryState);
        order.setDeliveryPostalCode(deliveryPostalCode);
        order.setDeliveryPhone(deliveryPhone);
        order.setDeliveryName(deliveryName);

        return orderRepository.save(order);
    }

    public List<Order> searchOrders(String searchTerm) {
        // جستجو بر اساس شماره سفارش یا ایمیل مشتری
        return orderRepository.findByCustomerEmail(searchTerm);
    }
}
