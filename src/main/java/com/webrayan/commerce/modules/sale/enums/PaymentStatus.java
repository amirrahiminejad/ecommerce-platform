package com.webrayan.commerce.modules.sale.enums;

public enum PaymentStatus {
    PENDING,           // در انتظار پرداخت
    PAID,              // پرداخت شده
    FAILED,            // پرداخت ناموفق
    CANCELLED,         // لغو شده
    REFUNDED,          // بازپرداخت شده
    PARTIAL_REFUNDED  // بازپرداخت جزئی
}
