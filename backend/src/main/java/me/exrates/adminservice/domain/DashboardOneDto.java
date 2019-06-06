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
public class DashboardOneDto {

    private BigDecimal exWalletBalancesUSDSum;
    private BigDecimal inWalletBalancesUSDSum;
    private BigDecimal deviationUSD;

    private int allCurrenciesCount;
    private int activeCurrenciesCount;
}