package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.api.RateDto;

import java.util.List;

public interface ExchangeRatesDao {

    List<RateDto> getAllExchangeRates();

    void updateCurrencyExchangeRates(List<RateDto> rates);
}
