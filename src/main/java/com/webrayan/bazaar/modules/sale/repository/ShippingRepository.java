package com.webrayan.bazaar.modules.sale.repository;

import com.webrayan.bazaar.modules.sale.entity.Shipping;
import com.webrayan.bazaar.modules.sale.enums.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    Optional<Shipping> findByOrderId(Long orderId);

    Optional<Shipping> findByTrackingNumber(String trackingNumber);

    List<Shipping> findByStatus(ShippingStatus status);

    List<Shipping> findByCarrierName(String carrierName);

    @Query("SELECT s FROM Shipping s WHERE s.estimatedDeliveryDate < :date AND s.status NOT IN ('DELIVERED', 'RETURNED_TO_SENDER')")
    List<Shipping> findOverdueShipments(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Shipping s WHERE s.shippedDate BETWEEN :startDate AND :endDate")
    List<Shipping> findByShippedDateBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Shipping s WHERE s.actualDeliveryDate BETWEEN :startDate AND :endDate")
    List<Shipping> findByDeliveryDateBetween(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Shipping s WHERE s.status = :status")
    Long countByStatus(@Param("status") ShippingStatus status);

    @Query("SELECT s FROM Shipping s WHERE LOWER(s.deliveryCity) = LOWER(:city)")
    List<Shipping> findByDeliveryCity(@Param("city") String city);

    @Query("SELECT s FROM Shipping s WHERE s.deliveryState = :state")
    List<Shipping> findByDeliveryState(@Param("state") String state);

    @Query("SELECT s FROM Shipping s WHERE s.status = 'SHIPPED' AND s.shippedDate < :cutoffDate")
    List<Shipping> findLongInTransitShipments(@Param("cutoffDate") LocalDateTime cutoffDate);

//    @Query("SELECT AVG(EXTRACT(DAY FROM (s.actualDeliveryDate - s.shippedDate))) FROM Shipping s WHERE s.status = 'DELIVERED' AND s.carrierName = :carrierName")
//    Double getAverageDeliveryTimeByCarrier(@Param("carrierName") String carrierName);

    @Query("SELECT s.carrierName, COUNT(s) FROM Shipping s WHERE s.status = 'DELIVERED' GROUP BY s.carrierName")
    List<Object[]> getCarrierStatistics();

    @Query("SELECT s FROM Shipping s WHERE s.signatureRequired = true AND s.status = 'OUT_FOR_DELIVERY'")
    List<Shipping> findSignatureRequiredDeliveries();
}
