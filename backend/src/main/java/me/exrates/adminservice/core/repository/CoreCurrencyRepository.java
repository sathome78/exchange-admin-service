package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.domain.CoreCurrencyPairDto;

import java.util.List;

public interface CoreCurrencyRepository {

    CoreCurrencyDto findCurrencyById(int id);

    CoreCurrencyDto findCurrencyByName(String name);

    List<CoreCurrencyDto> getAllCurrencies();

    List<CoreCurrencyDto> getActiveCurrencies();

    String getCurrencyName(int currencyId);
    
    CoreCurrencyPairDto findCurrencyPairById(int id);
}