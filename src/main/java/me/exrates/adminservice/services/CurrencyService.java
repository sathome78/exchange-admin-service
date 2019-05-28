package me.exrates.adminservice.services;


import me.exrates.adminservice.models.CurrencyDto;

import java.util.List;

public interface CurrencyService {

    CurrencyDto findByName(String name);

    List<CurrencyDto> getCachedCurrencies();
}