package me.exrates.adminservice.domain.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.serializers.BigDecimalSerializer;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInsightDTO {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("deposit")
    private BigDecimal deposit;

    @JsonProperty("withdrawal")
    private BigDecimal withdrawal;

    @JsonProperty("deposit_more10")
    private boolean depositGt10k;

    @JsonProperty("withdrawal_more10")
    private boolean withdrawGt10k;

    @JsonProperty("commission_trading_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComDay;

    @JsonProperty("commission_transfer_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComDay;

    @JsonProperty("commission_deposit_withdrawal_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal inoutComDay;

    @JsonProperty("commission_trading_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComWeek;

    @JsonProperty("commission_transfer_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComWeek;

    @JsonProperty("commission_deposit_withdrawal_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal inoutComWeek;

    @JsonProperty("commission_trading_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComMonth;

    @JsonProperty("commission_transfer_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComMonth;

    @JsonProperty("commission_deposit_withdrawal_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal inoutComMonth;

    @JsonProperty("commission_trading_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComYear;

    @JsonProperty("commission_transfer_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComYear;

    @JsonProperty("commission_deposit_withdrawal_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal inoutComYear;

    @JsonProperty("change_balance_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceDay;

    @JsonProperty("change_balance_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceWeek;

    @JsonProperty("change_balance_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceMonth;

    @JsonProperty("change_balance_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceYear;

    @JsonProperty("trade_number_per_day")
    private int tradeNumberDay;

    @JsonProperty("trade_amount_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountDay;

    @JsonProperty("trade_number_with_refill_per_day")
    private String tradeNumberWithRefillDay;

    @JsonProperty("trade_number_with_withdraw_per_day")
    private String tradeNumberWithWithdrawDay;

    @JsonProperty("trade_number_per_week")
    private int tradeNumberWeek;

    @JsonProperty("trade_amount_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountWeek;

    @JsonProperty("trade_number_with_refill_per_week")
    private String tradeNumberWithRefillWeek;

    @JsonProperty("trade_number_with_withdraw_per_week")
    private String tradeNumberWithWithdrawWeek;

    @JsonProperty("trade_number_per_month")
    private int tradeNumberMonth;

    @JsonProperty("trade_amount_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountMonth;

    @JsonProperty("trade_number_with_refill_per_month")
    private String tradeNumberWithRefillMonth;

    @JsonProperty("trade_number_with_withdraw_per_month")
    private String tradeNumberWithWithdrawMonth;

    @JsonProperty("trade_number_per_year")
    private int tradeNumberYear;

    @JsonProperty("trade_amount_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountYear;

    @JsonProperty("trade_number_with_refill_per_year")
    private String tradeNumberWithRefillYear;

    @JsonProperty("trade_number_with_withdraw_per_year")
    private String tradeNumberWithWithdrawYear;

    @JsonProperty("no_deals_but_refilled_per_day")
    private boolean noDealsButRefilledDay;

    @JsonProperty("no_deals_but_withdraw_and_refilled_per_day")
    private boolean noDealsButWithdrawAndRefilledDay;

    @JsonProperty("no_deals_but_refilled_per_week")
    private boolean noDealsButRefilledWeek;

    @JsonProperty("no_deals_but_withdraw_and_refilled_per_week")
    private boolean noDealsButWithdrawAndRefilledWeek;

    @JsonProperty("no_deals_but_refilled_per_month")
    private boolean noDealsButRefilledMonth;

    @JsonProperty("no_deals_but_withdraw_and_refilled_per_month")
    private boolean noDealsButWithdrawAndRefilledMonth;

    @JsonProperty("no_deals_but_refilled_per_year")
    private boolean noDealsButRefilledYear;

    @JsonProperty("no_deals_but_withdraw_and_refilled_per_year")
    private boolean noDealsButWithdrawAndRefilledYear;


    public static UserInsightDTO empty(int userId, String email) {
        return UserInsightDTO.builder()
                .userId(userId)
                .email(email)
                .deposit(BigDecimal.ZERO)
                .withdrawal(BigDecimal.ZERO)
                .tradeComDay(BigDecimal.ZERO)
                .transferComDay(BigDecimal.ZERO)
                .inoutComDay(BigDecimal.ZERO)
                .tradeComWeek(BigDecimal.ZERO)
                .transferComWeek(BigDecimal.ZERO)
                .inoutComWeek(BigDecimal.ZERO)
                .tradeComMonth(BigDecimal.ZERO)
                .transferComMonth(BigDecimal.ZERO)
                .inoutComMonth(BigDecimal.ZERO)
                .tradeComYear(BigDecimal.ZERO)
                .transferComYear(BigDecimal.ZERO)
                .inoutComYear(BigDecimal.ZERO)
                .changeBalanceDay(BigDecimal.ZERO)
                .changeBalanceWeek(BigDecimal.ZERO)
                .changeBalanceMonth(BigDecimal.ZERO)
                .changeBalanceYear(BigDecimal.ZERO)
                .tradeAmountDay(BigDecimal.ZERO)
                .tradeAmountWeek(BigDecimal.ZERO)
                .tradeAmountMonth(BigDecimal.ZERO)
                .tradeAmountYear(BigDecimal.ZERO)
                .build();
    }

}
