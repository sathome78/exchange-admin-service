package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
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
    private BigDecimal transferInAmountUsd;
    private BigDecimal transferOutAmountUsd;
    private BigDecimal transferCommissionUsd;
    private int tradeSellCount;
    private int tradeBuyCount;
    private BigDecimal tradeAmountUsd;
    private BigDecimal tradeCommissionUsd;
    private BigDecimal balanceDynamicsUsd;
    private List<Integer> sourceIds;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof UserInsight)) {
            return false;
        }
        UserInsight insight = (UserInsight) other;
        if (insight.getUserId().intValue() != this.getUserId()) {
            return false;
        }
        return insight.getCreated().compareTo(this.getCreated()) == 0;
    }
}
