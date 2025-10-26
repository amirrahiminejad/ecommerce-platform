package com.webrayan.store.modules.sale.dto;

import com.webrayan.store.modules.sale.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDto {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String gatewayName;
    private String redirectUrl; // برای پرداخت آنلاین
}
