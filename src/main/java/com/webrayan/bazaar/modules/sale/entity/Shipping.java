package com.webrayan.bazaar.modules.sale.entity;

import com.webrayan.bazaar.modules.sale.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_shipping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "tracking_number", unique = true, length = 100)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingStatus status = ShippingStatus.NOT_SHIPPED;

    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;

    @Column(name = "carrier_name", length = 100)
    private String carrierName;

    @Column(name = "shipping_cost", precision = 12, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "pickup_date")
    private LocalDateTime pickupDate;

    // آدرس تحویل (کپی از Order برای حفظ تاریخچه)
    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_city", length = 100)
    private String deliveryCity;

    @Column(name = "delivery_state", length = 100)
    private String deliveryState;

    @Column(name = "delivery_postal_code", length = 20)
    private String deliveryPostalCode;

    @Column(name = "delivery_phone", length = 20)
    private String deliveryPhone;

    @Column(name = "delivery_name", length = 100)
    private String deliveryName;

    @Column(name = "delivery_notes", columnDefinition = "TEXT")
    private String deliveryNotes;

    @Column(name = "weight", precision = 8, scale = 2)
    private BigDecimal weight; // کیلوگرم

    @Column(name = "dimensions", length = 100)
    private String dimensions; // طول x عرض x ارتفاع

    @Column(name = "insurance_amount", precision = 12, scale = 2)
    private BigDecimal insuranceAmount = BigDecimal.ZERO;

    @Column(name = "signature_required")
    private Boolean signatureRequired = false;

    @Column(name = "delivery_instructions", columnDefinition = "TEXT")
    private String deliveryInstructions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // متدهای کمکی
    public void ship(String trackingNumber, String carrierName) {
        this.trackingNumber = trackingNumber;
        this.carrierName = carrierName;
        this.status = ShippingStatus.SHIPPED;
        this.shippedDate = LocalDateTime.now();
    }

    public void markInTransit() {
        this.status = ShippingStatus.IN_TRANSIT;
    }

    public void markOutForDelivery() {
        this.status = ShippingStatus.OUT_FOR_DELIVERY;
    }

    public void markDelivered() {
        this.status = ShippingStatus.DELIVERED;
        this.actualDeliveryDate = LocalDateTime.now();
    }

    public void markFailedDelivery(String reason) {
        this.status = ShippingStatus.FAILED_DELIVERY;
        this.deliveryNotes = reason;
    }

    public void markReturnedToSender(String reason) {
        this.status = ShippingStatus.RETURNED_TO_SENDER;
        this.deliveryNotes = reason;
    }

    public boolean isDelivered() {
        return status == ShippingStatus.DELIVERED;
    }

    public boolean isInTransit() {
        return status == ShippingStatus.IN_TRANSIT || 
               status == ShippingStatus.OUT_FOR_DELIVERY ||
               status == ShippingStatus.SHIPPED;
    }
}
