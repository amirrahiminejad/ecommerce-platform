package com.webrayan.store.modules.sale.enums;

public enum OrderStatus {
    PENDING("در انتظار تایید"),           
    CONFIRMED("تایید شده"),         
    PROCESSING("در حال پردازش"),        
    SHIPPED("ارسال شده"),           
    DELIVERED("تحویل داده شده"),         
    CANCELLED("لغو شده"),         
    RETURNED("مرجوع شده"),          
    REFUNDED("بازپرداخت شده");

    private final String persianName;

    OrderStatus(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }
}
