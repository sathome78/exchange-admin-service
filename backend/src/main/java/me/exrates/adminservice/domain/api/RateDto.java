package me.exrates.adminservice.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {

    @JsonProperty("currency_name")
    private String currencyName;
    @JsonProperty("usd_rate")
    private BigDecimal usdRate;
    @JsonProperty("btc_rate")
    private BigDecimal btcRate;
    @JsonProperty("rate_btc_for_one_usd")
    private BigDecimal rateBtcForOneUsd;

    public static RateDto zeroRate(String currencyName) {
        return RateDto.builder()
                .currencyName(currencyName)
                .usdRate(BigDecimal.ZERO)
                .btcRate(BigDecimal.ZERO)
                .rateBtcForOneUsd(BigDecimal.ZERO)
                .build();
    }
}
