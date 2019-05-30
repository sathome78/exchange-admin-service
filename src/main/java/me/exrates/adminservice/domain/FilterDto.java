package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {

    private List<String> currencyNames;

    private BigDecimal minExBalance;
    private BigDecimal maxExBalance;

    private BigDecimal minInBalance;
    private BigDecimal maxInBalance;
}