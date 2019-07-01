package me.exrates.adminservice.services.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.domain.enums.RefillEventEnum;
import me.exrates.adminservice.events.TransactionsUpdateEvent;
import me.exrates.adminservice.repository.TransactionRepository;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.TransactionService;
import me.exrates.adminservice.utils.CurrencyTuple;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CoreTransactionRepository coreTransactionRepository;
    private final ExchangeRatesService exchangeRatesService;

    @Value("${sync.properties.transaction-chunk-size:20}")
    private int chunkSize;

    @Autowired
    public TransactionServiceImpl(TransactionRepository adminTransactionRepository,
                                  ApplicationEventPublisher applicationEventPublisher,
                                  CoreTransactionRepository coreTransactionRepository,
                                  ExchangeRatesService exchangeRatesService) {
        this.transactionRepository = adminTransactionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.coreTransactionRepository = coreTransactionRepository;
        this.exchangeRatesService = exchangeRatesService;
    }

    @Override
    public void syncTransactions() {
        final MutableBoolean shouldProceed = new MutableBoolean(false);
        final Set<Integer> updateUserIds = new HashSet<>();
        do {
            Optional<Long> result = transactionRepository.findMaxId();
            long lastIndex = result.orElse(-1L);
            final List<CoreTransaction> transactions = coreTransactionRepository.findAllLimited(chunkSize, lastIndex);
            shouldProceed.setValue(!transactions.isEmpty());

            if (shouldProceed.getValue()) {
                transactions.forEach(t -> {
                    updateUserIds.add(t.getUserId());
                    RateDto rateDto = exchangeRatesService.getCachedRates().getOrDefault(t.getCurrencyName(), RateDto.zeroRate(t.getCurrencyName()));
                    if (!(t.getCurrencyName().equalsIgnoreCase("BTC"))) {
                        t.setRateInBtc(rateDto.getBtcRate());
                    } else if (!(t.getCurrencyName().equalsIgnoreCase("USD"))) {
                        t.setRateInUsd(rateDto.getUsdRate());
                    }
                    t.setRateBtcForOneUsd(rateDto.getRateBtcForOneUsd());
                });
                transactionRepository.batchInsert(transactions);
            }
        } while (shouldProceed.getValue());
        if (!updateUserIds.isEmpty()) {
            applicationEventPublisher.publishEvent(new TransactionsUpdateEvent(this, updateUserIds));
        }
    }

    @Override
    public Map<String, BigDecimal> getDailyCommissionRevenue() {
        final Collection<CurrencyTuple> tradeCommissions = transactionRepository.getDailyTradeCommission();
        final BigDecimal btcAmount = reduce(tradeCommissions, CurrencyTuple::getBtcAmount);
        final BigDecimal usdAmount = reduce(tradeCommissions, CurrencyTuple::getUsdAmount);
        final Map<String, BigDecimal> result = new HashMap<>();
        result.put("USD", usdAmount);
        result.put("BTC", btcAmount);
        return result;
    }

    @Override
    public Map<String, BigDecimal> getDailyInnerTradeVolume() {
        final Map<String, BigDecimal> dailyInnerTradeVolume = transactionRepository.getDailyInnerTradeVolume();
        final Map<String, BigDecimal> result = new HashMap<>();
        result.put("USD", dailyInnerTradeVolume.getOrDefault("USD", BigDecimal.ZERO));
        result.put("BTC", dailyInnerTradeVolume.getOrDefault("BTC", BigDecimal.ZERO));
        return result;
    }

    @Override
    public Map<Integer, List<Integer>> getAllUsersRefills(Collection<Integer> usersIds) {
        Map<Integer, List<Integer>> refills = Maps.newHashMap();
        refills.putAll(transactionRepository.findUsersRefills(usersIds));
        usersIds.forEach(id -> refills.putIfAbsent(id, Collections.emptyList()));
        return refills;
    }

    @Override
    public Map<Integer, List<CoreTransaction>> findAllTransactions(Collection<Integer> userIds) {
        Map<Integer, List<CoreTransaction>> transactions = Maps.newHashMap();
        transactions.putAll(transactionRepository.findAllTransactions(userIds));
        userIds.forEach(id -> transactions.putIfAbsent(id, Lists.newArrayList()));
        return transactions;
    }

    @Override
    public Map<Integer, Set<RefillEventEnum>> getAllUsersRefillEvents(Map<Integer, List<CoreTransaction>> data,
                                                                      Collection<Integer> usersIds) {
        Map<Integer, Set<RefillEventEnum>> events = Maps.newHashMap();
        data.forEach((id, list) -> events.put(id, getEvents(list)));
        usersIds.forEach(id -> events.putIfAbsent(id, Sets.newHashSet()));
        return events;
    }

    private Set<RefillEventEnum> getEvents(List<CoreTransaction> transactions) {
        Set<RefillEventEnum> events = Sets.newHashSet();
        final List<CoreTransaction> sorted = transactions.stream()
                .sorted((o1, o2) -> o1.getId().compareTo(o2.getUserId()))
                .collect(Collectors.toList());
        sorted.forEach(tr -> {
            if (tr.getSourceType().equalsIgnoreCase("WITHDRAW") &&
                    tr.getBalanceBefore().compareTo(tr.getAmount().abs()) == 0) {
                events.add(RefillEventEnum.ZEROED);
            } else if (events.contains(RefillEventEnum.ZEROED) && tr.getSourceType().equalsIgnoreCase("REFILL")) {
                events.add(RefillEventEnum.REANIMATED);
            }
        });
        return events;
    }

    private BigDecimal reduce(Collection<CurrencyTuple> items, Function<CurrencyTuple, BigDecimal> mapFunction) {
        return items.stream().map(mapFunction).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
