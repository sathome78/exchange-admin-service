package me.exrates.adminservice.core.service;


import me.exrates.adminservice.core.domain.CoreCurrencyDto;

import java.util.List;

public interface CoreCurrencyService {

    CoreCurrencyDto findById(int id);

    CoreCurrencyDto findByName(String name);

    List<CoreCurrencyDto> getCachedCurrencies();

    List<CoreCurrencyDto> getActiveCachedCurrencies();

    List<String> getActiveCurrencyNames();

    String getCurrencyName(int currencyId);
}