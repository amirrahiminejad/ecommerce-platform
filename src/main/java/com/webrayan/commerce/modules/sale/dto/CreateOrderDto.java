package com.webrayan.commerce.modules.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    private Long customerId;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPostalCode;
    private String deliveryPhone;
    private String deliveryName;
    private String customerNotes;
    private String couponCode;
    private BigDecimal shippingCost;
}
