package me.exrates.adminservice.service;


import me.exrates.adminservice.domain.CurrencyDto;

import java.util.List;

public interface CurrencyService {

    CurrencyDto findByName(String name);

    List<CurrencyDto> getCachedCurrencies();
}
