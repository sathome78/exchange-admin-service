package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@EnableScheduling
@Component
public class CurrencyExchangeRatesHistoryJob {

    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public CurrencyExchangeRatesHistoryJob(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @Scheduled(cron = "${scheduled.update.rates-history}")
    public void updateCurrencyRateHistory() {
        exchangeRatesService.updateCurrencyExchangeRateHistory();
    }
}