package me.exrates.adminservice.service;


import me.exrates.adminservice.core.domain.CoreCurrencyDto;

import java.util.List;

public interface CurrencyService {

    CoreCurrencyDto findByName(String name);

    List<CoreCurrencyDto> getCachedCurrencies();
}