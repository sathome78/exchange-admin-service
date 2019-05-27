package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.api.RateDto;

import java.util.List;

public interface ExchangeRatesDao {

    RateDto getRateByCurrencyName(String currencyName);

    List<RateDto> getAllExchangeRates();

    boolean addCurrencyExchangeRates(RateDto rateDto);

    boolean updateCurrencyExchangeRates(RateDto rateDto);
}