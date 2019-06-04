package me.exrates.adminservice.services.impl;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.events.TransactionsUpdateEvent;
import me.exrates.adminservice.repository.AdminTransactionRepository;
import me.exrates.adminservice.repository.CursorRepository;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.SyncTransactionService;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SyncTransactionServiceImpl implements SyncTransactionService {

    private final AdminTransactionRepository adminTransactionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CursorRepository cursorRepository;
    private final CoreTransactionRepository coreTransactionRepository;
    private final ExchangeRatesService exchangeRatesService;

    @Value("${sync.properties.transaction-chunk-size:20}")
    private int chunkSize;

    @Autowired
    public SyncTransactionServiceImpl(AdminTransactionRepository adminTransactionRepository,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      @Qualifier(value = "cursorRepository") CursorRepository cursorRepository,
                                      CoreTransactionRepository coreTransactionRepository,
                                      ExchangeRatesService exchangeRatesService) {
        this.adminTransactionRepository = adminTransactionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cursorRepository = cursorRepository;
        this.coreTransactionRepository = coreTransactionRepository;
        this.exchangeRatesService = exchangeRatesService;
    }

    @Override
    public void syncTransactions() {
        final MutableBoolean shouldProceed = new MutableBoolean(false);
        final Set<Integer> updateUserIds = new HashSet<>();
        do {
            long lastIndex = cursorRepository.findLastByTable(CoreTransactionRepository.TABLE);
            final List<CoreTransaction> transactions = coreTransactionRepository.findAllLimited(chunkSize, lastIndex);
            shouldProceed.setValue(! transactions.isEmpty());

            if (shouldProceed.getValue()) {
                transactions.forEach(t -> {
                    updateUserIds.add(t.getUserId());
                    RateDto rateDto = exchangeRatesService.getCachedRates().getOrDefault(t.getCurrencyName(), RateDto.zeroRate(t.getCurrencyName()));
                    if (!(t.getCurrencyName().equalsIgnoreCase("BTC"))) {
                        t.setRateInBtc(rateDto.getBtcRate());
                    } else if (!(t.getCurrencyName().equalsIgnoreCase("USD"))) {
                        t.setRateInUsd(rateDto.getUsdRate());
                    }
                });
                adminTransactionRepository.batchInsert(transactions);
                cursorRepository.updateCursorByTable(CoreTransactionRepository.UPDATE_CURSOR_SQL);
            }
        } while (shouldProceed.getValue());
        if (! updateUserIds.isEmpty()) {
            applicationEventPublisher.publishEvent(new TransactionsUpdateEvent(this, updateUserIds));
        }
    }
}
