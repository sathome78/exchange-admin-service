package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.CurrencyDto;

import java.util.List;

public interface CurrencyDao {

    CurrencyDto findByName(String name);

    List<CurrencyDto> getAllCurrencies();
}
