package com.webrayan.bazaar.modules.sale.controller;

import com.webrayan.bazaar.modules.sale.service.ShippingService;
import com.webrayan.bazaar.modules.sale.entity.Shipping;
import com.webrayan.bazaar.modules.sale.enums.ShippingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/create")
    public ResponseEntity<Shipping> createShipping(
            @RequestParam Long orderId,
            @RequestParam String shippingMethod,
            @RequestParam String carrierName,
            @RequestParam BigDecimal shippingCost,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedDeliveryDate) {
        
        Shipping shipping = shippingService.createShipping(orderId, shippingMethod, carrierName, 
                shippingCost, estimatedDeliveryDate);
        return ResponseEntity.ok(shipping);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipping> getShipping(@PathVariable Long id) {
        Shipping shipping = shippingService.getShippingById(id);
        return ResponseEntity.ok(shipping);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipping> getShippingByOrder(@PathVariable Long orderId) {
        return shippingService.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Shipping> getShippingByTracking(@PathVariable String trackingNumber) {
        return shippingService.findByTrackingNumber(trackingNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shipping>> getShippingsByStatus(@PathVariable ShippingStatus status) {
        List<Shipping> shippings = shippingService.getShippingsByStatus(status);
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/carrier/{carrierName}")
    public ResponseEntity<List<Shipping>> getShippingsByCarrier(@PathVariable String carrierName) {
        List<Shipping> shippings = shippingService.getShippingsByCarrier(carrierName);
        return ResponseEntity.ok(shippings);
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<Shipping> shipOrder(@PathVariable Long id, @RequestParam String carrierName) {
        Shipping shipping = shippingService.shipOrder(id, carrierName);
        return ResponseEntity.ok(shipping);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Shipping> updateShippingStatus(
            @PathVariable Long id,
            @RequestParam ShippingStatus status,
            @RequestParam(required = false) String notes) {
        
        Shipping shipping = shippingService.updateShippingStatus(id, status, notes);
        return ResponseEntity.ok(shipping);
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<Shipping> updateTrackingNumber(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        
        Shipping shipping = shippingService.updateTrackingNumber(id, trackingNumber);
        return ResponseEntity.ok(shipping);
    }

    @PutMapping("/{id}/estimated-delivery")
    public ResponseEntity<Shipping> updateEstimatedDeliveryDate(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedDeliveryDate) {
        
        Shipping shipping = shippingService.updateEstimatedDeliveryDate(id, estimatedDeliveryDate);
        return ResponseEntity.ok(shipping);
    }

    @PutMapping("/{id}/delivery-instructions")
    public ResponseEntity<Shipping> updateDeliveryInstructions(
            @PathVariable Long id,
            @RequestParam String instructions) {
        
        Shipping shipping = shippingService.updateDeliveryInstructions(id, instructions);
        return ResponseEntity.ok(shipping);
    }

    @PutMapping("/{id}/signature-required")
    public ResponseEntity<Shipping> setSignatureRequired(
            @PathVariable Long id,
            @RequestParam boolean required) {
        
        Shipping shipping = shippingService.setSignatureRequired(id, required);
        return ResponseEntity.ok(shipping);
    }

    @PostMapping("/{id}/schedule-pickup")
    public ResponseEntity<Shipping> schedulePickup(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDate) {
        
        Shipping shipping = shippingService.schedulePickup(id, pickupDate);
        return ResponseEntity.ok(shipping);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Shipping>> getOverdueShipments() {
        List<Shipping> shippings = shippingService.getOverdueShipments();
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/long-transit")
    public ResponseEntity<List<Shipping>> getLongInTransitShipments(@RequestParam(defaultValue = "7") int days) {
        List<Shipping> shippings = shippingService.getLongInTransitShipments(days);
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/date-range/shipped")
    public ResponseEntity<List<Shipping>> getShippingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Shipping> shippings = shippingService.getShippingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/date-range/delivered")
    public ResponseEntity<List<Shipping>> getDeliveriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Shipping> shippings = shippingService.getDeliveriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Shipping>> getShippingsByCity(@PathVariable String city) {
        List<Shipping> shippings = shippingService.getShippingsByCity(city);
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<Shipping>> getShippingsByState(@PathVariable String state) {
        List<Shipping> shippings = shippingService.getShippingsByState(state);
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/today-deliveries")
    public ResponseEntity<List<Shipping>> getTodayDeliveries() {
        List<Shipping> shippings = shippingService.getTodayDeliveries();
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/signature-required")
    public ResponseEntity<List<Shipping>> getSignatureRequiredDeliveries() {
        List<Shipping> shippings = shippingService.getSignatureRequiredDeliveries();
        return ResponseEntity.ok(shippings);
    }

    @GetMapping("/{id}/status/delivered")
    public ResponseEntity<Boolean> isDelivered(@PathVariable Long id) {
        boolean delivered = shippingService.isDelivered(id);
        return ResponseEntity.ok(delivered);
    }

    @GetMapping("/{id}/status/in-transit")
    public ResponseEntity<Boolean> isInTransit(@PathVariable Long id) {
        boolean inTransit = shippingService.isInTransit(id);
        return ResponseEntity.ok(inTransit);
    }

    // آمارها و گزارشات
    @GetMapping("/statistics/count")
    public ResponseEntity<Map<ShippingStatus, Long>> getShippingCountByStatus() {
        Map<ShippingStatus, Long> statistics = Map.of(
                ShippingStatus.NOT_SHIPPED, shippingService.getShippingCountByStatus(ShippingStatus.NOT_SHIPPED),
                ShippingStatus.PREPARING, shippingService.getShippingCountByStatus(ShippingStatus.PREPARING),
                ShippingStatus.SHIPPED, shippingService.getShippingCountByStatus(ShippingStatus.SHIPPED),
                ShippingStatus.IN_TRANSIT, shippingService.getShippingCountByStatus(ShippingStatus.IN_TRANSIT),
                ShippingStatus.DELIVERED, shippingService.getShippingCountByStatus(ShippingStatus.DELIVERED),
                ShippingStatus.FAILED_DELIVERY, shippingService.getShippingCountByStatus(ShippingStatus.FAILED_DELIVERY)
        );
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/carriers")
    public ResponseEntity<List<Object[]>> getCarrierStatistics() {
        List<Object[]> statistics = shippingService.getCarrierStatistics();
        return ResponseEntity.ok(statistics);
    }

//    @GetMapping("/statistics/average-delivery-time/{carrierName}")
//    public ResponseEntity<Double> getAverageDeliveryTimeByCarrier(@PathVariable String carrierName) {
//        Double averageTime = shippingService.getAverageDeliveryTimeByCarrier(carrierName);
//        return ResponseEntity.ok(averageTime != null ? averageTime : 0.0);
//    }
}
