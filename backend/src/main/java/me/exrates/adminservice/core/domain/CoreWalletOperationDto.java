package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.TransactionSourceType;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoreWalletOperationDto implements Serializable {

    private OperationType operationType;
    private int walletId;
    private BigDecimal amount;
    private BalanceType balanceType;
    private CoreCommissionDto commission;
    private BigDecimal commissionAmount;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private CoreTransactionDto transaction;
    private String description;
    private int currencyId;

    public enum BalanceType {
        ACTIVE,
        RESERVED
    }
}