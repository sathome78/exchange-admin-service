package me.exrates.adminservice.services;


import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.RateDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExchangeRatesService {

    List<RateDto> getAllExchangeRates();

    Map<String, RateDto> getCachedRates();

    void updateCurrencyExchangeRates();

    void updateCurrencyExchangeRateHistory();

    List<RateHistoryDto> getExchangeRatesHistoryByDate(LocalDate date);
}