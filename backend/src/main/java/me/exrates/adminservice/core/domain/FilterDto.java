package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.domain.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FilterDto {

    private BigDecimal minBalance;
    private BigDecimal maxBalance;

    private LocalDate registeredFrom;
    private LocalDate registeredTo;

    private LocalDate lastEntryFrom;
    private LocalDate lastEntryTo;

    private boolean isVerified;
    private UserRole role;

    private boolean isActive;

    private List<String> currencies;

    private Integer minClosedOrders;
    private Integer maxClosedOrders;

    private Integer minRefillRequests;
    private Integer maxRefillRequests;

    private Integer minWithdrawRequests;
    private Integer maxWithdrawRequests;
}