package me.exrates.adminservice.service;

import me.exrates.adminservice.domain.CurrencyDto;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.domain.api.RateDto;

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
