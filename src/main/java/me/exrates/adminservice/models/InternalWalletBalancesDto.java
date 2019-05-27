package me.exrates.adminservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.models.enums.UserRole;
import me.exrates.adminservice.serializers.LocalDateTimeDeserializer;
import me.exrates.adminservice.serializers.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InternalWalletBalancesDto {

    private Integer currencyId;
    private String currencyName;

    private Integer roleId;
    private UserRole roleName;

    private BigDecimal usdRate;
    private BigDecimal btcRate;

    private BigDecimal totalBalance;
    private BigDecimal totalBalanceUSD;
    private BigDecimal totalBalanceBTC;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;
}