package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreCurrencyDto;

import java.util.List;

public interface CoreCurrencyRepository {

    CoreCurrencyDto findById(int id);

    CoreCurrencyDto findByName(String name);

    List<CoreCurrencyDto> getAllCurrencies();

    List<CoreCurrencyDto> getActiveCurrencies();

    String getCurrencyName(int currencyId);
}