package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.domain.enums.DeviationStatus;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class BalancesDto {

    private Integer currencyId;
    private String currencyName;

    private BigDecimal usdRate;
    private BigDecimal btcRate;

    private BigDecimal totalWalletBalance;
    private BigDecimal totalWalletBalanceUSD;
    private BigDecimal totalWalletBalanceBTC;

    private BigDecimal totalExratesBalance;
    private BigDecimal totalExratesBalanceUSD;
    private BigDecimal totalExratesBalanceBTC;

    private BigDecimal deviation;
    private BigDecimal deviationUSD;
    private BigDecimal deviationBTC;

    private boolean signOfMonitoring;

    private DeviationStatus deviationStatus;
}