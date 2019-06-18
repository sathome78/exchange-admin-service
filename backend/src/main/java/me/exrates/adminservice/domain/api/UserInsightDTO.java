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

    // ид пользователя
    @JsonProperty("user_id")
    private Integer userId;

    // email пользователя
    @JsonProperty("email")
    private String email;

    // Ввод (общая сумма в USD за все время)
    @JsonProperty("deposit")
    private BigDecimal deposit;

    // Вывод (общая сумма в USD за все время)
    @JsonProperty("withdrawal")
    private BigDecimal withdrawal;

    // Ввод более $10 000
    @JsonProperty("deposit_more10")
    private boolean depositGt10k;

    // Вывод более $10 000
    @JsonProperty("withdrawal_more10")
    private boolean withdrawGt10k;

    // Сумма комиссии (ТОРГИ) в $ за день
    @JsonProperty("commission_trading_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComDay;

    // Сумма комиссии (Внутр. ТРАНСФЕРА) в $ за день
    @JsonProperty("commission_transfer_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComDay;

    // Сумма комиссии (ВВОД/ВЫВОД) в $ за день
    @JsonProperty("commission_deposit_withdrawal_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal inoutComDay;

    // Сумма комиссии (ТОРГИ) $ за неделю
    @JsonProperty("commission_trading_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComWeek;

    // Сумма комиссии (Внутр. ТРАНСФЕР) $ за неделю
    @JsonProperty("commission_transfer_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComWeek;

    // Сумма комиссии (ВВОД/ВЫВОД) $ за неделю
    @JsonProperty("commission_deposit_withdrawal_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal inoutComWeek;

    // Сумма комиссии (ТОРГИ) $ за месяц
    @JsonProperty("commission_trading_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeComMonth;

    // Сумма комиссии (Внутр. ТРАНСФЕР) $ за месяц
    @JsonProperty("commission_transfer_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transferComMonth;

    // Сумма комиссии (ВВОД/ВЫВОД) $ за месяц 
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

    // Изменение баланса за день
    @JsonProperty("change_balance_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceDay;

    // Изменение баланса за неделю
    @JsonProperty("change_balance_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceWeek;

    // Изменение баланса за месяц
    @JsonProperty("commission_transfer_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceMonth;

    // Изменение баланса за год
    @JsonProperty("change_balance_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal changeBalanceYear;

    // Кол-во сделок за день
    @JsonProperty("trade_number_per_day")
    private int tradeNumberDay;

    // Сумма сделок за день
    @JsonProperty("trade_amount_per_day")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountDay;

    // Кол-во сделок за день + флаг (за период было снятия средств )
    @JsonProperty("trade_number_with_refill_per_day")
    private String tradeNumberWithRefillDay;

    // Кол-во сделок за день + флаг (за период было пополнение средств )
    @JsonProperty("trade_number_with_withdraw_per_day")
    private String tradeNumberWithWithdrawDay;

    // Кол-во сделок за неделю
    @JsonProperty("trade_number_per_week")
    private int tradeNumberWeek;

    // Сумма сделок за неделю
    @JsonProperty("trade_amount_per_week")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountWeek;

    // Кол-во сделок за неделю + флаг (за период было снятия средств )
    @JsonProperty("trade_number_with_refill_per_week")
    private String tradeNumberWithRefillWeek;

    // Кол-во сделок за неделю + флаг (за период было пополнение средств )
    @JsonProperty("trade_number_with_withdraw_per_week")
    private String tradeNumberWithWithdrawWeek;

    // Кол-во сделок за месяц
    @JsonProperty("trade_number_per_month")
    private int tradeNumberMonth;

    // Сумма сделок за месяц
    @JsonProperty("trade_amount_per_month")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountMonth;

    // Кол-во сделок за месяц + флаг (за период было снятия средств )
    @JsonProperty("trade_number_with_refill_per_month")
    private String tradeNumberWithRefillMonth;

    // Кол-во сделок за месяц + флаг (за период было пополнение средств )
    @JsonProperty("trade_number_with_withdraw_per_month")
    private String tradeNumberWithWithdrawMonth;

    // Кол-во сделок за год
    @JsonProperty("trade_number_per_year")
    private int tradeNumberYear;

    // Сумма сделок за год
    @JsonProperty("trade_amount_per_year")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmountYear;

    // Кол-во сделок за год + флаг (за период было снятия средств )
    @JsonProperty("trade_number_with_refill_per_year")
    private String tradeNumberWithRefillYear;

    // Кол-во сделок за год + флаг (за период было пополнение средств )
    @JsonProperty("trade_number_with_withdraw_per_year")
    private String tradeNumberWithWithdrawYear;

    // Сделки за день отсутствуют + (за период было пополнение средств но не совершено ордеров)
    @JsonProperty("no_deals_but_refilled_per_day")
    private boolean noDealsButRefilledDay;

    // Сделки за день отсутствуют + (за период было пополнение средств но не совершено ордеров + сделал операцию снятия)
    @JsonProperty("no_deals_but_withdraw_and_refilled_per_day")
    private boolean noDealsButWithdrawAndRefilledDay;

    // Сделки за неделю отсутствуют + (за период было пополнение средств но не совершено ордеров)
    @JsonProperty("no_deals_but_refilled_per_week")
    private boolean noDealsButRefilledWeek;

    // Сделки за неделю отсутствуют + (за период было пополнение средств но не совершено ордеров + сделал операцию снятия)
    @JsonProperty("no_deals_but_withdraw_and_refilled_per_week")
    private boolean noDealsButWithdrawAndRefilledWeek;

    // Сделки за месяц отсутствуют + (за период было пополнение средств но не совершено ордеров)
    @JsonProperty("no_deals_but_refilled_per_month")
    private boolean noDealsButRefilledMonth;

    // Сделки за месяц отсутствуют + (за период было пополнение средств но не совершено ордеров + сделал операцию снятия)
    @JsonProperty("no_deals_but_withdraw_and_refilled_per_month")
    private boolean noDealsButWithdrawAndRefilledMonth;

    // Сделки за год отсутствуют + (за период было пополнение средств но не совершено ордеров)
    @JsonProperty("no_deals_but_refilled_per_year")
    private boolean noDealsButRefilledYear;

    // Сделки за год отсутствуют + (за период было пополнение средств но не совершено ордеров + сделал операцию снятия)
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
