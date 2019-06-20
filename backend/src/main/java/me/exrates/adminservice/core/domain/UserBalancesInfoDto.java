package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserBalancesInfoDto {

    private String currencyName;
    private String lastRefillAddress;
    private BigDecimal summaryRefill;
    private String lastWithdrawAddress;
    private BigDecimal summaryWithdraw;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private BigDecimal commonBalance;
}