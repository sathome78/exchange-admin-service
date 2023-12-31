package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.utils.NonDevelopmentCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@EnableScheduling
@Component
@Conditional(NonDevelopmentCondition.class)
public class CurrencyExchangeRatesJob {

    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public CurrencyExchangeRatesJob(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void update() {
        try {
            exchangeRatesService.updateCurrencyExchangeRates();
        } catch (Exception ex) {
            log.error("--> In processing 'CurrencyExchangeRatesJob' occurred error", ex);
        }
    }
}
