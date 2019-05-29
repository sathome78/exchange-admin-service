package me.exrates.adminservice.service.impl;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.events.TransactionsUpdateEvent;
import me.exrates.adminservice.repository.AdminTransactionRepository;
import me.exrates.adminservice.repository.CursorRepository;
import me.exrates.adminservice.service.ExchangeRatesService;
import me.exrates.adminservice.service.SyncTransactionService;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

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
                                      ApplicationEventPublisher applicationEventPublisher, @Qualifier(value = "cursorRepository") CursorRepository cursorRepository,
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
        final MutableBoolean shouldProcced = new MutableBoolean(false);
        final MutableBoolean updateNeeded = new MutableBoolean(false);
        do {
            long lastIndex = cursorRepository.findLastByTable(CoreTransactionRepository.TABLE);
            final List<CoreTransaction> transactions = coreTransactionRepository.findAllLimited(chunkSize, lastIndex);
            shouldProcced.setValue(! transactions.isEmpty());

            if (!updateNeeded.getValue()) {
                updateNeeded.setValue(shouldProcced.getValue());
            }

            if (shouldProcced.getValue()) {
                transactions.forEach(t -> {
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
        } while (shouldProcced.getValue());
        if (updateNeeded.getValue()) {
            applicationEventPublisher.publishEvent(new TransactionsUpdateEvent(updateNeeded.booleanValue()));
        }
    }
}
