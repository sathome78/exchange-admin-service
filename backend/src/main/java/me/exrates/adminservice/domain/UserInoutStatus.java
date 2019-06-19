package me.exrates.adminservice.domain;

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
public class UserInoutStatus {

    private int userId;
    private BigDecimal refillAmountUsd;
    private BigDecimal withdrawAmountUsd;
    private LocalDateTime modified;

    public static UserInoutStatus empty(int userId) {
        return new UserInoutStatus(userId, BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now());
    }
}
