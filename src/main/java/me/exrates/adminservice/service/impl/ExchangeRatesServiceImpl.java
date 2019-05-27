package me.exrates.adminservice.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.ExchangeApi;
import me.exrates.adminservice.repository.ExchangeRatesDao;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.service.ExchangeRatesService;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static me.exrates.adminservice.config.CacheConfiguration.ALL_RATES_CACHE;
import static me.exrates.adminservice.util.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final ExchangeApi exchangeApi;
    private final ExchangeRatesDao exchangeRatesDao;
    private final Cache ratesCache;

    @Autowired
    public ExchangeRatesServiceImpl(ExchangeApi exchangeApi,
                                    ExchangeRatesDao exchangeRatesDao,
                                    @Qualifier(ALL_RATES_CACHE) Cache ratesCache) {
        this.exchangeApi = exchangeApi;
        this.exchangeRatesDao = exchangeRatesDao;
        this.ratesCache = ratesCache;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RateDto> getAllExchangeRates() {
        return exchangeRatesDao.getAllExchangeRates();
    }

    @Override
    public Map<String, RateDto> getCachedRates() {
        return Objects.requireNonNull(ratesCache.get(ALL_RATES_CACHE, this::getAllExchangeRates)).stream()
                .collect(toMap(RateDto::getCurrencyName, Function.identity()));
    }

    @Override
    public void updateCurrencyExchangeRates() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency exchange rates start...");

        final List<RateDto> rates = exchangeApi.getRatesFromApi();
        if (isEmpty(rates)) {
            return;
        }
        for (RateDto rateDto : rates) {
            RateDto oldRateDto = exchangeRatesDao.getRateByCurrencyName(rateDto.getCurrencyName());
            if (isNull(oldRateDto)) {
                boolean inserted = exchangeRatesDao.addCurrencyExchangeRates(rateDto);
                log.debug("Process of add new exchange rates for currency: {} finished with result: {}", rateDto.getCurrencyName(), inserted);
            } else {
                if (oldRateDto.getUsdRate().compareTo(rateDto.getUsdRate()) == 0 && oldRateDto.getBtcRate().compareTo(rateDto.getBtcRate()) == 0) {
                    continue;
                }
                boolean updated = exchangeRatesDao.updateCurrencyExchangeRates(rateDto);
                log.debug("Process of update exchange rates for currency: {} finished with result: {}", rateDto.getCurrencyName(), updated);
            }
        }
        log.info("Process of updating currency exchange rates end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}
