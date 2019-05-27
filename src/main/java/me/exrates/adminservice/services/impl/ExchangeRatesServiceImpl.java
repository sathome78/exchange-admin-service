package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.ExchangeApi;
import me.exrates.adminservice.daos.ExchangeRatesDao;
import me.exrates.adminservice.models.api.RateDto;
import me.exrates.adminservice.services.ExchangeRatesService;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static me.exrates.adminservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final ExchangeApi exchangeApi;
    private final ExchangeRatesDao exchangeRatesDao;

    @Autowired
    public ExchangeRatesServiceImpl(ExchangeApi exchangeApi,
                                    ExchangeRatesDao exchangeRatesDao) {
        this.exchangeApi = exchangeApi;
        this.exchangeRatesDao = exchangeRatesDao;
    }

    @Override
    public void updateCurrencyExchangeRates() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency exchange rates start...");

        final List<RateDto> rates = exchangeApi.getRatesFromApi();
        if (isEmpty(rates)) {
            return;
        }
        rates.forEach(rateDto -> {
            RateDto oldRateDto = exchangeRatesDao.getRateByCurrencyName(rateDto.getCurrencyName());
            if (isNull(oldRateDto)) {
                boolean inserted = exchangeRatesDao.addCurrencyExchangeRates(rateDto);
                log.debug("Process of add new exchange rates for currency: {} finished with result: {}", rateDto.getCurrencyName(), inserted);
            } else {
                boolean updated = exchangeRatesDao.updateCurrencyExchangeRates(rateDto);
                log.debug("Process of update exchange rates for currency: {} finished with result: {}", rateDto.getCurrencyName(), updated);
            }
        });
        log.info("Process of updating currency exchange rates end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}