package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@EnableScheduling
@Component
public class CurrencyExchangeRatesJob {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyExchangeRatesJob(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void update() {
        currencyService.updateCurrencyExchangeRates();
    }
}