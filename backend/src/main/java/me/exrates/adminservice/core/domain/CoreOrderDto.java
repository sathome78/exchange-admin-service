package me.exrates.adminservice.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.OrderBaseType;
import me.exrates.adminservice.core.domain.enums.OrderEvent;
import me.exrates.adminservice.core.domain.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
public class CoreOrderDto implements Serializable {

    private int id;
    private int userId;
    private int currencyPairId;
    private OperationType operationType;
    private BigDecimal exRate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private int comissionId;
    private BigDecimal commissionFixedAmount;
    private int userAcceptorId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateAcception;
    private OrderStatus status;
    private CoreCurrencyPairDto currencyPair;
    private Integer sourceId;
    private BigDecimal stop;
    private OrderBaseType orderBaseType = OrderBaseType.LIMIT;
    private BigDecimal partiallyAcceptedAmount;
    @JsonIgnore
    private Long tradeId;
    @JsonIgnore
    private OrderEvent event;
    private long eventTimestamp;

    public CoreOrderDto(CoreOrderCreateDto orderCreateDto) {
        this.id = orderCreateDto.getOrderId();
        this.userId = orderCreateDto.getUserId();
        this.currencyPairId = orderCreateDto.getCurrencyPair().getId();
        this.operationType = orderCreateDto.getOperationType();
        this.exRate = orderCreateDto.getExchangeRate();
        this.amountBase = orderCreateDto.getAmount();
        this.amountConvert = orderCreateDto.getTotal();
        this.comissionId = orderCreateDto.getComissionId();
        this.commissionFixedAmount = orderCreateDto.getComission();
        this.status = orderCreateDto.getStatus();
        this.currencyPair = orderCreateDto.getCurrencyPair();
        this.sourceId = orderCreateDto.getSourceId();
        this.stop = orderCreateDto.getStop();
        this.orderBaseType = orderCreateDto.getOrderBaseType();
        this.tradeId = orderCreateDto.getTradeId();
    }
}