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

    @JsonProperty("last_visit")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastVisit;

    @JsonProperty("not_refilled_coins_48_hours")
    private int notRefilledCoins48Hours;

    @JsonProperty("not_refilled_coins_7_days")
    private int notRefilledCoins7Days;

    @JsonProperty("not_refilled_coins_30_days")
    private int notRefilledCoins30Days;

    @JsonProperty("not_refilled_coins_90_days")
    private int notRefilledCoins90Days;


}
