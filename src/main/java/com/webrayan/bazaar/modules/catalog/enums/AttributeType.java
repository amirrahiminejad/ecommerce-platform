package com.webrayan.bazaar.modules.catalog.enums;

public enum AttributeType {
    TEXT("متن"),
    NUMBER("عدد"),
    DECIMAL("اعشار"),
    BOOLEAN("بولی"),
    DATE("تاریخ"),
    DATETIME("تاریخ و زمان"),
    EMAIL("ایمیل"),
    URL("آدرس وب"),
    PHONE("تلفن"),
    SELECT("انتخاب از لیست"),
    MULTI_SELECT("انتخاب چندگانه"),
    RADIO("رادیو"),
    CHECKBOX("چک باکس"),
    TEXTAREA("متن طولانی"),
    FILE("فایل"),
    IMAGE("تصویر"),
    COLOR("رنگ"),
    JSON("JSON");

    private final String displayName;

    AttributeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
