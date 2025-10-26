package com.webrayan.store.modules.catalog.enums;

public enum ProductStatus {
    DRAFT("پیش‌نویس"),
    PENDING("در انتظار تأیید"),
    APPROVED("تأیید شده"),
    PUBLISHED("منتشر شده"),
    REJECTED("رد شده"),
    SUSPENDED("تعلیق"),
    OUT_OF_STOCK("ناموجود"),
    DISCONTINUED("متوقف شده");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
