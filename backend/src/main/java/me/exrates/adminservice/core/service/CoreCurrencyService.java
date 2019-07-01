package me.exrates.adminservice.core.service;


import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.domain.CoreCurrencyPairDto;

import java.util.List;

public interface CoreCurrencyService {

    CoreCurrencyDto findCachedCurrencyById(int id);

    CoreCurrencyDto findCachedCurrencyByName(String name);

    List<CoreCurrencyDto> getCachedCurrencies();

    List<CoreCurrencyDto> getCachedActiveCurrencies();

    List<String> getActiveCurrencyNames();

    String getCurrencyName(int currencyId);
    
    CoreCurrencyPairDto findCachedCurrencyPairById(int id);
}