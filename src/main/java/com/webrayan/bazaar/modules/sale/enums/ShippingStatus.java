package com.webrayan.bazaar.modules.sale.enums;

public enum ShippingStatus {
    NOT_SHIPPED,       // ارسال نشده
    PREPARING,         // در حال آماده‌سازی
    SHIPPED,           // ارسال شده
    IN_TRANSIT,        // در حال حمل
    OUT_FOR_DELIVERY,  // خروج برای تحویل
    DELIVERED,         // تحویل شده
    FAILED_DELIVERY,   // تحویل ناموفق
    RETURNED_TO_SENDER // بازگشت به فرستنده
}
