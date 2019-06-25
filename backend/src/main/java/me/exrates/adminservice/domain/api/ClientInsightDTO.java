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

    @JsonProperty("first_refill_no_ops_24_hours")
    private boolean firstRefillNoOps24Hours;

    @JsonProperty("first_refill_no_ops_7_days")
    private boolean firstRefillNoOps7Days;

    @JsonProperty("first_refill_no_ops_30_days")
    private boolean firstRefillNoOps30Days;

    @JsonProperty("first_refill_no_ops_90_days")
    private boolean firstRefillNoOps90Days;

    @JsonProperty("first_refill_no_ops_365_days")
    private boolean firstRefillNoOps365Days;

    @JsonProperty("first_refill")
    private boolean firstRefill;

    @JsonProperty("twin_refill")
    private boolean twinRefill;

    @JsonProperty("refill_and_trade")
    private boolean refillAndTrade;


}
