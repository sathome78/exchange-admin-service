package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreCurrencyDto;

import java.util.List;

public interface CoreCurrencyRepository {

    CoreCurrencyDto findByName(String name);

    List<CoreCurrencyDto> getAllCurrencies();
}