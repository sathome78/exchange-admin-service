package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.api.RateDto;

import java.util.List;

public interface ExchangeRatesDao {

    RateDto getRateByCurrencyName(String currencyName);

    List<RateDto> getAllExchangeRates();

    boolean addCurrencyExchangeRates(RateDto rateDto);

    boolean updateCurrencyExchangeRates(RateDto rateDto);
}
