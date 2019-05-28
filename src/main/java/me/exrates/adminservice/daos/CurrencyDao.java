package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.CurrencyDto;

import java.util.List;

public interface CurrencyDao {

    CurrencyDto findByName(String name);

    List<CurrencyDto> getAllCurrencies();
}
