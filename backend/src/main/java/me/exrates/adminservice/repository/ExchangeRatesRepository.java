package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.RateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRatesRepository {

    String TABLE_NAME_1 = "CURRENT_CURRENCY_RATES";
    String TABLE_NAME_2 = "CURRENCY_RATES_HISTORY";

    List<RateDto> getAllExchangeRates();

    void updateCurrencyExchangeRates(List<RateDto> rates);

    void saveCurrencyExchangeRatesHistory(byte[] zippedBytes);

    List<RateHistoryDto> getExchangeRatesHistoryByDate(LocalDateTime fromDate, LocalDateTime toDate);
}