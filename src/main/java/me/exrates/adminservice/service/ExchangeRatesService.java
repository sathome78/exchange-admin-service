package me.exrates.adminservice.service;


import me.exrates.adminservice.domain.api.RateDto;

import java.util.List;
import java.util.Map;

public interface ExchangeRatesService {

    List<RateDto> getAllExchangeRates();

    Map<String, RateDto> getCachedRates();

    void updateCurrencyExchangeRates();
}