package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInsight {

    private LocalDate created;
    private Integer userId;
    private BigDecimal rateBtcForOneUsd;
    private BigDecimal refillAmountUsd;
    private BigDecimal withdrawAmountUsd;
    private BigDecimal inoutCommissionUsd;
    private BigDecimal transferAmountUsd;
    private BigDecimal transferCommissionUsd;
    private BigDecimal tradeAmountUsd;
    private BigDecimal tradeCommissionUsd;
    private BigDecimal balanceDynamicsUsd;
    private List<Integer> sourceIds;
}
