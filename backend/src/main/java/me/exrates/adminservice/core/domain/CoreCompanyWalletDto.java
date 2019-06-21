package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoreCompanyWalletDto implements Serializable {

    private int id;
    private CoreCurrencyDto currency;
    private BigDecimal balance;
    private BigDecimal commissionBalance;
}