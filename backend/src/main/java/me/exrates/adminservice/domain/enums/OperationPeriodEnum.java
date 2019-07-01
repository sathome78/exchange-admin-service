package me.exrates.adminservice.domain.enums;

import java.time.LocalDateTime;
import java.util.Arrays;

public enum OperationPeriodEnum {

    LAST_DAY(1),
    LAST_2_DAYS(2),
    LAST_7_DAYS(7),
    LAST_30_DAYS(30),
    LAST_90_DAYS(90),
    LAST_365_DAYS(365),
    LAST_3_YEARS(1095),
    LAST_5_YEARS(1825);


    private final int days;

    OperationPeriodEnum(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    public static LocalDateTime getBound(OperationPeriodEnum period) {
        if (Arrays.stream(OperationPeriodEnum.values()).anyMatch(p -> p == period)) {
            return LocalDateTime.now().minusDays(period.getDays());
        }
        throw new UnsupportedOperationException("OperationPeriodEnum " + period + " not allowed");
    }
}
