package me.exrates.adminservice.core.domain;

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
public class CoreTransaction {

    private Integer id;
    private Integer userId;
    private String currencyName;
    private BigDecimal balanceBefore;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private String sourceType;
    private String operationType;
    private LocalDateTime dateTime;
    private BigDecimal rateInUsd;
    private BigDecimal rateInBtc;
    private BigDecimal rateBtcForOneUsd;
    private Integer sourceId;

}
