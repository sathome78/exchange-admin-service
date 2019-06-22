package me.exrates.adminservice.domain.enums;

public enum  RefillAddressEnum {

    LAST_48_DAYS(2), LAST_7_DAYS(7), LAST_30_DAYS(30), LAST_90_DAYS(90);

    private final int days;

    RefillAddressEnum(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
