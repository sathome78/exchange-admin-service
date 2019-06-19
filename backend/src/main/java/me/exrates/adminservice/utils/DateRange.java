package me.exrates.adminservice.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRange {

    private LocalDate startDate;
    private LocalDate endDate;


//    public DateRange getCurrentWeek() {
//
//    }
//
//    public DateRange getCurrentMonth() {
//
//    }
//
//    public DateRange getCurrentYear() {
//        LocalDate now = LocalDate.now();
//        now.withYear()
//    }
}
