package me.exrates.adminservice.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.ActionType;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.OrderBaseType;
import me.exrates.adminservice.core.domain.enums.OrderStatus;

import java.math.BigDecimal;

import static me.exrates.adminservice.utils.BigDecimalProcessingUtil.doAction;

@Data
@NoArgsConstructor
public class CoreOrderCreateDto {

    private int orderId;
    private int userId;
    private OrderStatus status;

    private CoreCurrencyPairDto currencyPair;
    private int comissionForBuyId;
    private BigDecimal comissionForBuyRate;
    private int comissionForSellId;
    private BigDecimal comissionForSellRate;
    private int walletIdCurrencyBase;
    private BigDecimal currencyBaseBalance;
    private int walletIdCurrencyConvert;
    private BigDecimal currencyConvertBalance;

    private BigDecimal stop; //stop rate for stop order
    private OperationType operationType;
    private BigDecimal exchangeRate;
    private BigDecimal amount; //amount of base currency: base currency can be bought or sold dependending on operationType
    private OrderBaseType orderBaseType;

    private BigDecimal spentWalletBalance;
    private BigDecimal spentAmount;
    private BigDecimal total; //calculated amount of currency conversion = amount * exchangeRate
    private int comissionId;
    private BigDecimal comission; //calculated comission amount depending on operationType and corresponding comission rate
    private BigDecimal totalWithComission; //total + comission
    private Integer sourceId;
    private Long tradeId;

    public CoreOrderCreateDto calculateAmounts() {
        if (operationType == null) {
            return this;
        }
        if (operationType == OperationType.SELL) {
            this.spentWalletBalance = this.currencyBaseBalance == null ? BigDecimal.ZERO : this.currencyBaseBalance;
            this.total = doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
            this.comissionId = this.comissionForSellId;
            this.comission = doAction(this.total, this.comissionForSellRate, ActionType.MULTIPLY_PERCENT);
            this.totalWithComission = doAction(this.total, this.comission.negate(), ActionType.ADD);
            this.spentAmount = this.amount;
        } else if (operationType == OperationType.BUY) {
            this.spentWalletBalance = this.currencyConvertBalance == null ? BigDecimal.ZERO : this.currencyConvertBalance;
            this.total = doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
            this.comissionId = this.comissionForBuyId;
            this.comission = doAction(this.total, this.comissionForBuyRate, ActionType.MULTIPLY_PERCENT);
            this.totalWithComission = doAction(this.total, this.comission, ActionType.ADD);
            this.spentAmount = doAction(this.total, this.comission, ActionType.ADD);
        }
        return this;
    }
}