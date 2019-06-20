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
public class RiskManagementBoardDTO {

    @JsonProperty("pie_chart_sell")
    public int diagramSellPercentage;

    @JsonProperty("pie_chart_buy")
    public int diagramBuyPercentage;

    @JsonProperty("coverage_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal tradeBotCoverageBTC;

    @JsonProperty("coverage_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal tradeBotCoverageUSD;

    @JsonProperty("inner_trade_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal innerTradeVolumeDayBTC;

    @JsonProperty("inner_trade_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal innerTradeVolumeDayUSD;

    @JsonProperty("bot_commission_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal outerCommissionBTC;

    @JsonProperty("bot_commission_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal outerCommissionUSD;

    @JsonProperty("trade_revenue_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal tradeIncomeBTC;

    @JsonProperty("trade_revenue_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal tradeIncomeUSD;

    @JsonProperty("commission_revenue_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal commissionRevenueBTC;

    @JsonProperty("commission_revenue_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    public BigDecimal commissionRevenueUSD;

    @JsonProperty("unique_clients_quantity")
    public int uniqueClientsQuantity;
}
