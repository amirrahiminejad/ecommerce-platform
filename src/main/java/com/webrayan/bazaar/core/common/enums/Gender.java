package com.webrayan.bazaar.core.common.enums;

public enum Gender {
    MALE("مرد"),
    FEMALE("زن"),
    OTHER("سایر");

    private final String persianName;

    Gender(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }
}
