package me.exrates.adminservice.core.domain;

import lombok.Data;
import me.exrates.adminservice.core.domain.enums.OperationType;

import java.math.BigDecimal;

@Data
public class CoreWithdrawRequestParamsDto {

    private Integer currency;
    private Integer merchant;
    private BigDecimal sum;
    private String destination;
    private String destinationTag;
    private int merchantImage;
    private OperationType operationType;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private String walletNumber;
    private String securityCode;
}