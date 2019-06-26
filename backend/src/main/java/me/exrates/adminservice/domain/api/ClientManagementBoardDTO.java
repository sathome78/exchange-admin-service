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
public class ClientManagementBoardDTO {

    @JsonProperty("pie_chart_sell")
    private int diagramSellPercentage;

    @JsonProperty("pie_chart_buy")
    private int diagramBuyPercentage;

    @JsonProperty("coverage_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeBotCoverageBTC;

    @JsonProperty("coverage_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeBotCoverageUSD;

    @JsonProperty("inner_trade_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal innerTradeVolumeDayBTC;

    @JsonProperty("inner_trade_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal innerTradeVolumeDayUSD;

    @JsonProperty("bot_commission_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal outerCommissionBTC;

    @JsonProperty("bot_commission_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal outerCommissionUSD;

    @JsonProperty("trade_revenue_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeIncomeBTC;

    @JsonProperty("trade_revenue_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeIncomeUSD;

    @JsonProperty("commission_revenue_btc")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commissionRevenueBTC;

    @JsonProperty("commission_revenue_usd")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal commissionRevenueUSD;

    @JsonProperty("unique_clients_quantity")
    private int uniqueClientsQuantity;
}
