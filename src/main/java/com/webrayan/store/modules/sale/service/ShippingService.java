package com.webrayan.store.modules.sale.service;

import com.webrayan.store.modules.sale.entity.Shipping;
import com.webrayan.store.modules.sale.entity.Order;
import com.webrayan.store.modules.sale.enums.ShippingStatus;
import com.webrayan.store.modules.sale.repository.ShippingRepository;
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
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Shipping createShipping(Long orderId, String shippingMethod, String carrierName, 
                                 BigDecimal shippingCost, LocalDateTime estimatedDeliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setStatus(ShippingStatus.PREPARING);
        shipping.setShippingMethod(shippingMethod);
        shipping.setCarrierName(carrierName);
        shipping.setShippingCost(shippingCost);
        shipping.setEstimatedDeliveryDate(estimatedDeliveryDate);
        
        // کپی آدرس تحویل از سفارش
        shipping.setDeliveryAddress(order.getDeliveryAddress());
        shipping.setDeliveryCity(order.getDeliveryCity());
        shipping.setDeliveryState(order.getDeliveryState());
        shipping.setDeliveryPostalCode(order.getDeliveryPostalCode());
        shipping.setDeliveryPhone(order.getDeliveryPhone());
        shipping.setDeliveryName(order.getDeliveryName());

        return shippingRepository.save(shipping);
    }

    @Transactional
    public Shipping shipOrder(Long shippingId, String carrierName) {
        Shipping shipping = getShippingById(shippingId);
        String trackingNumber = generateTrackingNumber();
        
        shipping.ship(trackingNumber, carrierName);
        return shippingRepository.save(shipping);
    }

    @Transactional
    public Shipping updateShippingStatus(Long shippingId, ShippingStatus status, String notes) {
        Shipping shipping = getShippingById(shippingId);
        
        switch (status) {
            case IN_TRANSIT:
                shipping.markInTransit();
                break;
            case OUT_FOR_DELIVERY:
                shipping.markOutForDelivery();
                break;
            case DELIVERED:
                shipping.markDelivered();
                break;
            case FAILED_DELIVERY:
                shipping.markFailedDelivery(notes);
                break;
            case RETURNED_TO_SENDER:
                shipping.markReturnedToSender(notes);
                break;
            default:
                shipping.setStatus(status);
        }
        
        if (notes != null && !notes.isEmpty()) {
            shipping.setDeliveryNotes(notes);
        }

        return shippingRepository.save(shipping);
    }

    public Shipping getShippingById(Long id) {
        return shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping not found"));
    }

    public Optional<Shipping> findByOrderId(Long orderId) {
        return shippingRepository.findByOrderId(orderId);
    }

    public Optional<Shipping> findByTrackingNumber(String trackingNumber) {
        return shippingRepository.findByTrackingNumber(trackingNumber);
    }

    public List<Shipping> getShippingsByStatus(ShippingStatus status) {
        return shippingRepository.findByStatus(status);
    }

    public List<Shipping> getShippingsByCarrier(String carrierName) {
        return shippingRepository.findByCarrierName(carrierName);
    }

    public List<Shipping> getOverdueShipments() {
        return shippingRepository.findOverdueShipments(LocalDateTime.now());
    }

    public List<Shipping> getShippingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return shippingRepository.findByShippedDateBetween(startDate, endDate);
    }

    public List<Shipping> getDeliveriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return shippingRepository.findByDeliveryDateBetween(startDate, endDate);
    }

    public Long getShippingCountByStatus(ShippingStatus status) {
        return shippingRepository.countByStatus(status);
    }

    public List<Shipping> getShippingsByCity(String city) {
        return shippingRepository.findByDeliveryCity(city);
    }

    public List<Shipping> getShippingsByState(String state) {
        return shippingRepository.findByDeliveryState(state);
    }

    public List<Shipping> getLongInTransitShipments(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return shippingRepository.findLongInTransitShipments(cutoffDate);
    }

//    public Double getAverageDeliveryTimeByCarrier(String carrierName) {
//        return shippingRepository.getAverageDeliveryTimeByCarrier(carrierName);
//    }

    public List<Object[]> getCarrierStatistics() {
        return shippingRepository.getCarrierStatistics();
    }

    public List<Shipping> getSignatureRequiredDeliveries() {
        return shippingRepository.findSignatureRequiredDeliveries();
    }

    @Transactional
    public Shipping updateTrackingNumber(Long shippingId, String trackingNumber) {
        Shipping shipping = getShippingById(shippingId);
        shipping.setTrackingNumber(trackingNumber);
        return shippingRepository.save(shipping);
    }

    @Transactional
    public Shipping updateEstimatedDeliveryDate(Long shippingId, LocalDateTime estimatedDeliveryDate) {
        Shipping shipping = getShippingById(shippingId);
        shipping.setEstimatedDeliveryDate(estimatedDeliveryDate);
        return shippingRepository.save(shipping);
    }

    @Transactional
    public Shipping updateDeliveryInstructions(Long shippingId, String instructions) {
        Shipping shipping = getShippingById(shippingId);
        shipping.setDeliveryInstructions(instructions);
        return shippingRepository.save(shipping);
    }

    @Transactional
    public Shipping setSignatureRequired(Long shippingId, boolean required) {
        Shipping shipping = getShippingById(shippingId);
        shipping.setSignatureRequired(required);
        return shippingRepository.save(shipping);
    }

    public boolean isDelivered(Long shippingId) {
        Shipping shipping = getShippingById(shippingId);
        return shipping.isDelivered();
    }

    public boolean isInTransit(Long shippingId) {
        Shipping shipping = getShippingById(shippingId);
        return shipping.isInTransit();
    }

    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Shipping schedulePickup(Long shippingId, LocalDateTime pickupDate) {
        Shipping shipping = getShippingById(shippingId);
        shipping.setPickupDate(pickupDate);
        shipping.setStatus(ShippingStatus.PREPARING);
        return shippingRepository.save(shipping);
    }

    public List<Shipping> getTodayDeliveries() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return shippingRepository.findByDeliveryDateBetween(startOfDay, endOfDay);
    }
}
