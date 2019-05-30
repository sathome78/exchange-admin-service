package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class DashboardTwoDto {

    private BigDecimal exWalletBalancesUSDSum;
    private BigDecimal inWalletBalancesUSDSum;
    private BigDecimal deviationUSD;

    private BigDecimal exWalletBalancesBTCSum;
    private BigDecimal inWalletBalancesBTCSum;
    private BigDecimal deviationBTC;

    private int inRangeCount;
    private int outRangeCount;
    private int withoutRangeCount;

    private int activeCurrenciesCount;
    private int monitoredCurrenciesCount;
}