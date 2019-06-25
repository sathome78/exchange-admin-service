package me.exrates.adminservice.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.serializers.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientInsightDTO {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("messenger")
    private String messenger;

    @JsonProperty("last_login")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastLogin;

    @JsonProperty("not_refilled_coins_48_hours")
    private int notRefilledCoins48Hours;

    @JsonProperty("not_refilled_coins_7_days")
    private int notRefilledCoins7Days;

    @JsonProperty("not_refilled_coins_30_days")
    private int notRefilledCoins30Days;

    @JsonProperty("not_refilled_coins_90_days")
    private int notRefilledCoins90Days;

    @JsonProperty("first_refill_no_ops_48_hours")
    private boolean firstRefillNoOps48Hours;

    @JsonProperty("first_refill_no_ops_7_days")
    private boolean firstRefillNoOps7Days;

    @JsonProperty("first_refill_no_ops_30_days")
    private boolean firstRefillNoOps30Days;

    @JsonProperty("first_refill_no_ops_90_days")
    private boolean firstRefillNoOps90Days;

    @JsonProperty("first_refill")
    private boolean firstRefill;

    @JsonProperty("twin_refill")
    private boolean twinRefill;

    @JsonProperty("refill_and_trade")
    private boolean refillAndTrade;

    @JsonProperty("zero_account")
    private boolean zeroedBalance;

    @JsonProperty("reanimate_account")
    private boolean reanimateBalance;

    @JsonProperty("trades_24_hours")
    private boolean hasTradesIn24Hours;

    @JsonProperty("trades_7_days")
    private boolean hasTradesIn7Days;

    @JsonProperty("trades_30_days")
    private boolean hasTradesIn30Days;

    @JsonProperty("trades_90_days")
    private boolean hasTradesIn90Days;

    @JsonProperty("trades_365_days")
    private boolean hasTradesIn365Days;

    @JsonProperty("refill_basic")
    private boolean hasRefillBasic;

    @JsonProperty("refill_profi")
    private boolean hasRefillProfi;

    @JsonProperty("refill_gold")
    private boolean hasRefillGold;

    @JsonProperty("refill_executive")
    private boolean hasRefillExecutive;

    @JsonProperty("trades_basic")
    private boolean hasTradesBasic;

    @JsonProperty("trades_profi")
    private boolean hasTradesProfi;

    @JsonProperty("trades_gold")
    private boolean hasTradesGold;

    @JsonProperty("trades_executive")
    private boolean hasTradesExecutive;

    @JsonProperty("no_trades_24_hours")
    private boolean hasNoTradesIn24Hours;

    @JsonProperty("no_trades_7_days")
    private boolean hasNoTradesIn7Days;

    @JsonProperty("no_trades_30_days")
    private boolean hasNoTradesIn30Days;

    @JsonProperty("no_trades_90_days")
    private boolean hasNoTradesIn90Days;

    @JsonProperty("trades_24_hours_withdraw")
    private boolean hasNoTradesIn24HoursWithdraw;

    @JsonProperty("trades_7_days_withdraw")
    private boolean hasNoTradesIn7DaysWithdraw;

    @JsonProperty("trades_30_days_withdraw")
    private boolean hasNoTradesIn30DaysWithdraw;

    @JsonProperty("trades_90_days_withdraw")
    private boolean hasNoTradesIn90DaysWithdraw;

    @JsonProperty("trades_reanimated_24_hours")
    private boolean hasTradesReanimatedIn24Hours;

    @JsonProperty("trades_reanimated_30_days")
    private boolean hasTradesReanimatedIn30Days;

    @JsonProperty("trades_reanimated_90_days")
    private boolean hasTradesReanimatedIn90Days;

    @JsonProperty("trades_reanimated_1_year")
    private boolean hasTradesReanimatedIn1Year;

    @JsonProperty("trades_reanimated_3_year")
    private boolean hasTradesReanimatedIn3Years;

    @JsonProperty("trades_reanimated_5_years")
    private boolean hasTradesReanimatedIn5Years;

}
