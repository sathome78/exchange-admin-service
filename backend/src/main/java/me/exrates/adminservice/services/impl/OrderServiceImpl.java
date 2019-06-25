package me.exrates.adminservice.services.impl;

import com.google.common.collect.Maps;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import me.exrates.adminservice.domain.ClosedOrder;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.ClosedOrderRepository;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.OrderService;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final ClosedOrderRepository closedOrderRepository;
    private final ExchangeRatesService exchangeRatesService;
    private final CoreOrderRepository coreOrderRepository;

    @Value("${sync.properties.transaction-chunk-size:20}")
    private int chunkSize;

    @Autowired
    public OrderServiceImpl(ClosedOrderRepository closedOrderRepository,
                            ExchangeRatesService exchangeRatesService,
                            CoreOrderRepository coreOrderRepository) {
        this.closedOrderRepository = closedOrderRepository;
        this.exchangeRatesService = exchangeRatesService;
        this.coreOrderRepository = coreOrderRepository;
    }

    @Override
    public void syncOrders() {
        final MutableBoolean shouldProceed = new MutableBoolean(false);
        do {
            final int maxId = closedOrderRepository.findMaxId().orElse(-1);
            final List<ClosedOrder> orders = coreOrderRepository.findAllLimited(chunkSize, maxId);
            shouldProceed.setValue(!orders.isEmpty());
            if (shouldProceed.getValue()) {
                orders.forEach(o -> {
                    String currencyName = getCurrencyName(o);
                    if (currencyName.equalsIgnoreCase("BTC")) {
                        o.setAmountUsd(o.getAmountConvert());
                    } else {
                        RateDto rateDto = exchangeRatesService.getCachedRates().getOrDefault(currencyName, RateDto.zeroRate(currencyName));
                        o.setAmountUsd(rateDto.getUsdRate().multiply(o.getAmountConvert()));
                    }
                });
                closedOrderRepository.batchInsert(orders);
            }
        } while (shouldProceed.getValue());
    }

    @Override
    public Map<Integer, List<Integer>> getAllUserClosedOrders(Collection<Integer> userIds) {
        Map<Integer, List<Integer>> closedOrders = Maps.newHashMap();
        closedOrders.putAll(closedOrderRepository.getAllUserClosedOrders(userIds));
        userIds.forEach(id -> closedOrders.putIfAbsent(id, Collections.emptyList()));
        return closedOrders;
    }

    private String getCurrencyName(ClosedOrder closedOrder) {
        final String pairName = closedOrder.getCurrencyPairName();
        return pairName.substring(pairName.lastIndexOf("/") + 1);
    }
}
