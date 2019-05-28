package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.api.RateDto;

import java.util.List;

public interface ExchangeRatesDao {

    List<RateDto> getAllExchangeRates();

    void updateCurrencyExchangeRates(List<RateDto> rates);
}