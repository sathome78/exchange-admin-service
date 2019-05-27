package me.exrates.adminservice.services;


import me.exrates.adminservice.models.CurrencyDto;
import me.exrates.adminservice.models.api.BalanceDto;
import me.exrates.adminservice.models.api.RateDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CurrencyService {

    CurrencyDto findByName(String name);

    List<CurrencyDto> getAllCurrencies();

    Map<String, RateDto> getRates();

    Map<String, BalanceDto> getBalances();

    List<RateDto> getCurrencyRates();

    List<BalanceDto> getCurrencyBalances();

    Map<String, BigDecimal> getCurrencyReservedBalances();

    void updateCurrencyExchangeRates();

    void updateCurrencyBalances();

    List<CurrencyDto> findAllCurrenciesWithHidden();

    void updateWithdrawLimits();
}