package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.RateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRatesRepository {

    List<RateDto> getAllExchangeRates();

    void updateCurrencyExchangeRates(List<RateDto> rates);

    void saveCurrencyExchangeRatesHistory(byte[] zippedBytes);

    List<RateHistoryDto> getExchangeRatesHistoryByDate(LocalDateTime fromDate, LocalDateTime toDate);
}