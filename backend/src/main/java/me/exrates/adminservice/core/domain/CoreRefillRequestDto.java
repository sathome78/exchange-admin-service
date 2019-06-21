package me.exrates.adminservice.core.domain;

import lombok.Data;
import me.exrates.adminservice.core.domain.enums.RefillStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CoreRefillRequestDto implements Serializable {

    private int id;
    private String address;
    private Integer userId;
    private String payerBankName;
    private String payerBankCode;
    private String payerAccount;
    private String userFullName;
    private String remark;
    private String receiptScan;
    private String receiptScanName;
    private BigDecimal amount;
    private Integer commissionId;
    private RefillStatusEnum status;
    private LocalDateTime dateCreation;
    private LocalDateTime statusModificationDate;
    private Integer currencyId;
    private Integer merchantId;
    private String merchantTransactionId;
    private String recipientBankName;
    private Integer recipientBankId;
    private String recipientBankAccount;
    private String recipientBankRecipient;
    private Integer adminHolderId;
    private Integer confirmations;
}