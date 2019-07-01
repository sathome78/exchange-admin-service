package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClosedOrder {

    private int id;
    private String currencyPairName;
    private int creatorId;
    private int acceptorId;
    private BigDecimal rate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private BigDecimal amountUsd;
    private LocalDate closedDate;
    private String baseType;
}
