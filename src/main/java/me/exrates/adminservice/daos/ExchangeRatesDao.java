package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.api.RateDto;

public interface ExchangeRatesDao {

    RateDto getRateByCurrencyName(String currencyName);

    boolean addCurrencyExchangeRates(RateDto rateDto);

    boolean updateCurrencyExchangeRates(RateDto rateDto);
}