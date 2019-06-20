package me.exrates.adminservice.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTuple {

    private String currencyName;
    private BigDecimal usdAmount;
    private BigDecimal btcAmount;
    private BigDecimal rateBtcForOneUsd;

    public static CurrencyTuple zeroed(String currencyName) {
        return CurrencyTuple.builder()
                .currencyName(currencyName)
                .usdAmount(BigDecimal.ZERO)
                .btcAmount(BigDecimal.ZERO)
                .rateBtcForOneUsd(BigDecimal.ZERO)
                .build();
    }
}
