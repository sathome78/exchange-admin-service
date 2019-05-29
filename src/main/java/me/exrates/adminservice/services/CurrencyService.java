package me.exrates.adminservice.services;


import me.exrates.adminservice.core.domain.CoreCurrencyDto;

import java.util.List;

public interface CurrencyService {

    CoreCurrencyDto findById(int id);

    CoreCurrencyDto findByName(String name);

    List<CoreCurrencyDto> getCachedCurrencies();

    String getCurrencyName(int currencyId);
}