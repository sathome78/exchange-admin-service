package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreTransaction {

    private Integer id;
    private Integer userId;
    private String currencyName;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private String sourceType;
    private String operationType;
    private LocalDateTime dateTime;
    private BigDecimal rateInUsd;
    private BigDecimal rateInBtc;
}
