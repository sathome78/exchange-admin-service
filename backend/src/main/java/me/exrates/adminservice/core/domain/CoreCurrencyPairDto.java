package me.exrates.adminservice.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.CurrencyPairType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreCurrencyPairDto implements Serializable {
    private int id;
    private String name;
    private CoreCurrencyDto currency1;
    private CoreCurrencyDto currency2;
    private String market;
    private String marketName;
    private CurrencyPairType pairType;
    private boolean hidden;
    private boolean permittedLink;

    public CoreCurrencyPairDto(CoreCurrencyDto currency1, CoreCurrencyDto currency2) {
        this.currency1 = currency1;
        this.currency2 = currency2;
    }

    public CoreCurrencyPairDto(String currencyPairName) {
        this.name = currencyPairName;
    }

    public CoreCurrencyDto getAnotherCurrency(CoreCurrencyDto currency) {
        return currency.equals(currency1) ? currency2 : currency1;
    }
}