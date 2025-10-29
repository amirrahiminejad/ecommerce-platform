package com.webrayan.store.component.data.initializers;

import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.repository.UserRepository;
import com.webrayan.store.modules.catalog.entity.Product;
import com.webrayan.store.modules.catalog.repository.ProductRepository;
import com.webrayan.store.modules.sale.entity.Order;
import com.webrayan.store.modules.sale.entity.OrderItem;
import com.webrayan.store.modules.sale.enums.OrderStatus;
import com.webrayan.store.modules.sale.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaleDataInitializer {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    private final Random random = new Random();

    @Transactional
    public void initialize() {
        if (orderRepository.count() > 0) {
            log.info("📦 Sale data already exists, skipping initialization...");
            return;
        }

        log.info("📦 Initializing Sale data...");
        
        try {
            // دریافت کاربران با نقش CUSTOMER
            List<User> customers = userRepository.findByRole(Role.RoleName.CUSTOMER);
            if (customers.isEmpty()) {
                log.warn("⚠️ No customers found for order initialization");
                return;
            }

            // دریافت محصولات فعال
            List<Product> products = productRepository.findAll();
            if (products.isEmpty()) {
                log.warn("⚠️ No products found for order initialization");
                return;
            }

            // ایجاد سفارشات نمونه
            createSampleOrders(customers, products);
            
            log.info("✅ Sale data initialized successfully");
            
        } catch (Exception e) {
            log.error("❌ Error initializing sale data: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void createSampleOrders(List<User> customers, List<Product> products) {
        // سفارش 1: تحویل شده
        Order order1 = createOrder(
            customers.get(0),
            "ORD-2025-001",
            OrderStatus.DELIVERED,
            LocalDateTime.now().minusDays(10),
            "تهران، خیابان ولیعصر، پلاک 123",
            "تهران",
            "تهران", 
            "1234567890",
            "+98912345678",
            "علی احمدی",
            "سفارش فوری لطفاً",
            null
        );
        addOrderItems(order1, products, 2);
        orderRepository.save(order1);

        // سفارش 2: در حال پردازش
        Order order2 = createOrder(
            customers.get(Math.min(1, customers.size()-1)),
            "ORD-2025-002", 
            OrderStatus.PROCESSING,
            LocalDateTime.now().minusDays(3),
            "اصفهان، خیابان چهارباغ، کوچه سوم، پلاک 45",
            "اصفهان",
            "اصفهان",
            "8765432109",
            "+98913456789",
            "مریم رضایی", 
            "ارسال عادی",
            null
        );
        addOrderItems(order2, products, 1);
        orderRepository.save(order2);

        // سفارش 3: تأیید شده
        Order order3 = createOrder(
            customers.get(Math.min(2, customers.size()-1)),
            "ORD-2025-003",
            OrderStatus.CONFIRMED,
            LocalDateTime.now().minusDays(1),
            "شیراز، خیابان کریم خان زند، مجتمع تجاری پارس، واحد 12",
            "شیراز",
            "فارس",
            "1122334455", 
            "+98914567890",
            "حسن کریمی",
            null,
            null
        );
        addOrderItems(order3, products, 3);
        orderRepository.save(order3);

        // سفارش 4: ارسال شده
        Order order4 = createOrder(
            customers.get(0),
            "ORD-2025-004",
            OrderStatus.SHIPPED,
            LocalDateTime.now().minusDays(5),
            "مشهد، خیابان امام رضا، کوچه بهشت، پلاک 67",
            "مشهد", 
            "خراسان رضوی",
            "9988776655",
            "+98915678901",
            "فاطمه محمدی",
            "لطفاً با احتیاط حمل شود",
            null
        );
        addOrderItems(order4, products, 1);
        orderRepository.save(order4);

        // سفارش 5: لغو شده
        Order order5 = createOrder(
            customers.get(Math.min(1, customers.size()-1)),
            "ORD-2025-005",
            OrderStatus.CANCELLED,
            LocalDateTime.now().minusDays(7),
            "تبریز، خیابان بازار، مغازه شماره 15",
            "تبریز",
            "آذربایجان شرقی", 
            "5566778899",
            "+98916789012",
            "رضا نوری",
            null,
            "عدم موجودی کالا"
        );
        addOrderItems(order5, products, 2);
        orderRepository.save(order5);

        // سفارش 6: در انتظار پرداخت
        Order order6 = createOrder(
            customers.get(Math.min(2, customers.size()-1)),
            "ORD-2025-006",
            OrderStatus.PENDING,
            LocalDateTime.now().minusHours(6),
            "کرج، میدان آزادی، خیابان طالقانی، پلاک 89",
            "کرج",
            "البرز",
            "4433221100", 
            "+98917890123",
            "زهرا احمدزاده",
            "تماس قبل از ارسال",
            null
        );
        addOrderItems(order6, products, 1);
        orderRepository.save(order6);

        // سفارش 7: در حال پردازش (آماده ارسال)
        Order order7 = createOrder(
            customers.get(0),
            "ORD-2025-007",
            OrderStatus.PROCESSING,
            LocalDateTime.now().minusDays(2),
            "بندرعباس، خیابان ساحل، برج دریا، طبقه 5، واحد 12",
            "بندرعباس",
            "هرمزگان",
            "7788990011",
            "+98918901234", 
            "محمد رستمی",
            "ارسال فوری",
            null
        );
        addOrderItems(order7, products, 4);
        orderRepository.save(order7);

        // سفارش 8: تحویل شده (با کد تخفیف)
        Order order8 = createOrder(
            customers.get(Math.min(1, customers.size()-1)),
            "ORD-2025-008",
            OrderStatus.DELIVERED,
            LocalDateTime.now().minusDays(15),
            "اهواز، خیابان کیانپارس، مجتمع مسکونی نور، بلوک B، واحد 25",
            "اهواز", 
            "خوزستان",
            "6677889900",
            "+98919012345",
            "سارا موسوی",
            "تحویل در ساعات اداری",
            null
        );
        addOrderItems(order8, products, 2);
        order8.setCouponCode("SUMMER2025");
        order8.setDiscountAmount(BigDecimal.valueOf(50000));
        recalculateOrder(order8);
        orderRepository.save(order8);

        // سفارش 9: در حال پردازش (سفارش بزرگ)
        Order order9 = createOrder(
            customers.get(Math.min(2, customers.size()-1)),
            "ORD-2025-009",
            OrderStatus.PROCESSING,
            LocalDateTime.now().minusHours(12),
            "قم، خیابان معلم، کوچه شهید باهنر، پلاک 33",
            "قم",
            "قم",
            "3344556677",
            "+98920123456",
            "امین حسینی", 
            "سفارش شرکتی - ارسال با بسته‌بندی ویژه",
            "مشتری VIP - اولویت بالا"
        );
        addOrderItems(order9, products, 5);
        orderRepository.save(order9);

        // سفارش 10: تأیید شده (آدرس طولانی)
        Order order10 = createOrder(
            customers.get(0),
            "ORD-2025-010",
            OrderStatus.CONFIRMED,
            LocalDateTime.now().minusHours(18),
            "یزد، بلوار آیت الله کاشانی، کوی دانشگاه، خیابان استاد مطهری، کوچه گلستان، پلاک 156، طبقه دوم، واحد شمالی",
            "یزد",
            "یزد",
            "2233445566", 
            "+98921234567",
            "الهام صادقی",
            "لطفاً کد پستی را چک کنید و قبل از ارسال تماس بگیرید. ارسال ترجیحاً صبح باشد.",
            "آدرس تأیید شده - تماس گرفته شد"
        );
        addOrderItems(order10, products, 3);
        orderRepository.save(order10);

        log.info("📦 Created 10 sample orders with different statuses");
    }

    private Order createOrder(User customer, String orderNumber, OrderStatus status, 
                             LocalDateTime orderDate, String deliveryAddress, String deliveryCity,
                             String deliveryState, String deliveryPostalCode, String deliveryPhone,
                             String deliveryName, String customerNotes, String cancellationReason) {
        
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomer(customer);
        order.setStatus(status);
        order.setOrderDate(orderDate);
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryCity(deliveryCity);
        order.setDeliveryState(deliveryState);
        order.setDeliveryPostalCode(deliveryPostalCode);
        order.setDeliveryPhone(deliveryPhone);
        order.setDeliveryName(deliveryName);
        order.setCustomerNotes(customerNotes);
        order.setCurrency("تومان");
        
        // تنظیم تاریخ‌های مربوط به وضعیت
        switch (status) {
            case CONFIRMED:
                order.setConfirmedAt(orderDate.plusHours(2));
                break;
            case PROCESSING:
                order.setConfirmedAt(orderDate.plusHours(1));
                break;
            case SHIPPED:
                order.setConfirmedAt(orderDate.plusHours(1));
                order.setShippedAt(orderDate.plusDays(1));
                break;
            case DELIVERED:
                order.setConfirmedAt(orderDate.plusHours(1));
                order.setShippedAt(orderDate.plusDays(1));
                order.setDeliveredAt(orderDate.plusDays(3));
                break;
            case CANCELLED:
                order.setCancelledAt(orderDate.plusHours(6));
                order.setCancellationReason(cancellationReason);
                break;
            case PENDING:
            case RETURNED:
            case REFUNDED:
                // این وضعیت‌ها نیاز به تنظیم تاریخ خاص ندارند
                break;
        }
        
        // تنظیم هزینه حمل و مالیات
        order.setShippingCost(BigDecimal.valueOf(25000)); // 25 هزار تومان حمل
        order.setTaxAmount(BigDecimal.ZERO); // بدون مالیات
        order.setDiscountAmount(BigDecimal.ZERO);
        
        return order;
    }

    private void addOrderItems(Order order, List<Product> products, int itemCount) {
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (int i = 0; i < itemCount && i < products.size(); i++) {
            Product product = products.get(random.nextInt(products.size()));
            
            // اطمینان از عدم تکرار محصول در همان سفارش
            boolean productExists = order.getOrderItems().stream()
                    .anyMatch(item -> item.getProduct().getId().equals(product.getId()));
            
            if (productExists) {
                // محصول دیگری انتخاب کن
                continue;
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            
            // تعداد تصادفی بین 1 تا 3
            int quantity = random.nextInt(3) + 1;
            orderItem.setQuantity(quantity);
            
            // قیمت محصول (با تغییرات کمی برای شبیه‌سازی تغییرات قیمت)
            BigDecimal basePrice = product.getPrice();
            BigDecimal variation = basePrice.multiply(BigDecimal.valueOf(random.nextDouble() * 0.1 - 0.05)); // ±5%
            BigDecimal unitPrice = basePrice.add(variation);
            orderItem.setUnitPrice(unitPrice);
            
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            orderItem.setTotalPrice(totalPrice);
            orderItem.setDiscountAmount(BigDecimal.ZERO);
            orderItem.setTaxAmount(BigDecimal.ZERO);
            
            // اطلاعات محصول در زمان سفارش
            orderItem.setProductDescription(product.getShortDescription());
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                orderItem.setProductImageUrl(product.getImages().get(0).getImage().getUrl());
            }
            
            order.addOrderItem(orderItem);
            subtotal = subtotal.add(totalPrice);
        }
        
        order.setSubtotal(subtotal);
        recalculateOrder(order);
    }

    private void recalculateOrder(Order order) {
        BigDecimal subtotal = order.getSubtotal();
        BigDecimal shipping = order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.ZERO;
        BigDecimal tax = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        
        BigDecimal total = subtotal.add(shipping).add(tax).subtract(discount);
        order.setTotalAmount(total);
    }
}
