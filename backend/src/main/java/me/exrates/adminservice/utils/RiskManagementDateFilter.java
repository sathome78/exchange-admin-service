package me.exrates.adminservice.utils;

import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Data
public class RiskManagementDateFilter {

    private final int DAY;
    private final int WEEK;
    private final int MONTH;
    private final int YEAR;

    public static RiskManagementDateFilter createDefault() {
        LocalDate now = LocalDate.now();
        TemporalField week = WeekFields.of(Locale.getDefault()).weekOfYear();
        return new RiskManagementDateFilter(now.getDayOfYear(), now.get(week), now.getMonthValue(), now.getYear());
    }
}
