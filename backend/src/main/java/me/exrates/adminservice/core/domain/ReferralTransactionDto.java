package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReferralTransactionDto {

    private int initiatorId;
    private String initiatorEmail;
    private int referralLevel;
    private BigDecimal referralPercent;
    private int orderId;
}