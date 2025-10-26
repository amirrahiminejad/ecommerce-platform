package com.webrayan.commerce.modules.sale.enums;

public enum OrderStatus {
    PENDING,           // در انتظار تایید
    CONFIRMED,         // تایید شده
    PROCESSING,        // در حال پردازش
    SHIPPED,           // ارسال شده
    DELIVERED,         // تحویل داده شده
    CANCELLED,         // لغو شده
    RETURNED,          // مرجوع شده
    REFUNDED          // بازپرداخت شده
}
