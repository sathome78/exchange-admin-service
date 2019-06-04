package me.exrates.adminservice.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.ExchangeApi;
import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.ExchangeRatesRepository;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.utils.ZipUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.zip.DataFormatException;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_RATES_CACHE;
import static me.exrates.adminservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final ExchangeApi exchangeApi;
    private final ExchangeRatesRepository exchangeRatesRepository;
    private final Cache ratesCache;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExchangeRatesServiceImpl(ExchangeApi exchangeApi,
                                    ExchangeRatesRepository exchangeRatesRepository,
                                    @Qualifier(ALL_RATES_CACHE) Cache ratesCache,
                                    @Qualifier("jsonMapper") ObjectMapper objectMapper) {
        this.exchangeApi = exchangeApi;
        this.exchangeRatesRepository = exchangeRatesRepository;
        this.ratesCache = ratesCache;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RateDto> getAllExchangeRates() {
        return exchangeRatesRepository.getAllExchangeRates();
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
        exchangeRatesRepository.updateCurrencyExchangeRates(rates);
        log.info("Process of updating currency exchange rates end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateCurrencyExchangeRateHistory() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency exchange rates history start...");

        final List<RateDto> rates = this.getAllExchangeRates();

        byte[] zippedBytes;
        try {
            byte[] ratesBytes = objectMapper.writeValueAsBytes(rates);

            zippedBytes = ZipUtil.zip(ratesBytes);
        } catch (IOException ex) {
            log.warn("Problem with write rates object to byte array", ex);
            return;
        }
        exchangeRatesRepository.saveCurrencyExchangeRatesHistory(zippedBytes);

        log.info("Process of updating currency exchange rates history end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RateHistoryDto> getExchangeRatesHistoryByDate(LocalDate date) {
        return exchangeRatesRepository.getExchangeRatesHistoryByDate(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX)).stream()
                .peek(history -> {
                    final byte[] content = history.getContent();

                    try {
                        byte[] ratesBytes = ZipUtil.unzip(content);

                        List<RateDto> rates = objectMapper.readValue(ratesBytes, new TypeReference<List<RateDto>>() {
                        });
                        history.setRates(rates);
                    } catch (IOException | DataFormatException ex) {
                        log.warn("Problem with write rates object from byte array", ex);
                        history.setRates(Collections.emptyList());
                    }
                })
                .collect(toList());
    }
}