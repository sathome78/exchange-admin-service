package me.exrates.adminservice.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {

    @JsonProperty("currency_name")
    private String currencyName;
    private BigDecimal balance;
    @JsonProperty("last_updated_at")
    private LocalDateTime lastUpdatedAt;

    public static BalanceDto zeroBalance(String currencyName) {
        return BalanceDto.builder()
                .currencyName(currencyName)
                .balance(BigDecimal.ZERO)
                .lastUpdatedAt(null)
                .build();
    }
}