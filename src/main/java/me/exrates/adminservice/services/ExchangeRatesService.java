package me.exrates.adminservice.services;

import me.exrates.adminservice.models.api.RateDto;

import java.util.List;
import java.util.Map;

public interface ExchangeRatesService {

    List<RateDto> getAllExchangeRates();

    Map<String, RateDto> getCachedRates();

    void updateCurrencyExchangeRates();
}
