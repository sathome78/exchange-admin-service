package me.exrates.adminservice.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.InvoiceStatus;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.TransactionSourceType;
import me.exrates.adminservice.serializers.LocalDateTimeSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoreTransactionDto implements Serializable {
    
    private int id;
    private CoreWalletDto userWallet;
    private CoreCompanyWalletDto companyWallet;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private CoreCommissionDto commission;
    private OperationType operationType;
    private CoreCurrencyDto currency;
    private CoreMerchantDto merchant;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private CoreOrderDto order;
    private boolean provided;
    private Integer confirmation;
    private BigDecimal activeBalanceBefore;
    private BigDecimal reservedBalanceBefore;
    private BigDecimal companyBalanceBefore;
    private BigDecimal companyCommissionBalanceBefore;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private String description;
    private CoreWithdrawRequestDto withdrawRequest;
    private CoreRefillRequestDto refillRequest;
    private InvoiceStatus invoiceStatus;
}