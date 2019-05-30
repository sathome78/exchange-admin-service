package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCurrencyRepository;
import me.exrates.adminservice.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_CURRENCIES_CACHE;
import static me.exrates.adminservice.configurations.CacheConfiguration.CURRENCY_CACHE_BY_ID;
import static me.exrates.adminservice.configurations.CacheConfiguration.CURRENCY_CACHE_BY_NAME;

@Log4j2
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CoreCurrencyRepository coreCurrencyRepository;
    private final Cache allCurrenciesCache;
    private final Cache currencyCacheByName;
    private final Cache currencyCacheById;

    @Autowired
    public CurrencyServiceImpl(CoreCurrencyRepository coreCurrencyRepository,
                               @Qualifier(CURRENCY_CACHE_BY_NAME) Cache currencyCacheByName,
                               @Qualifier(CURRENCY_CACHE_BY_ID) Cache currencyCacheById,
                               @Qualifier(ALL_CURRENCIES_CACHE) Cache allCurrenciesCache) {
        this.coreCurrencyRepository = coreCurrencyRepository;
        this.allCurrenciesCache = allCurrenciesCache;
        this.currencyCacheByName = currencyCacheByName;
        this.currencyCacheById = currencyCacheById;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCurrencyDto findById(int id) {
        return currencyCacheById.get(id, () -> coreCurrencyRepository.findById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCurrencyDto findByName(String name) {
        return currencyCacheByName.get(name, () -> coreCurrencyRepository.findByName(name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoreCurrencyDto> getCachedCurrencies() {
        return allCurrenciesCache.get(ALL_CURRENCIES_CACHE, coreCurrencyRepository::getAllCurrencies);
    }

    @Override
    public List<CoreCurrencyDto> getActiveCachedCurrencies() {
        return this.getCachedCurrencies().stream()
                .filter(currency -> !currency.isHidden())
                .collect(toList());
    }

    @Override
    public List<String> getActiveCurrencyNames() {
        return this.getCachedCurrencies().stream()
                .filter(currency -> !currency.isHidden())
                .map(CoreCurrencyDto::getName)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public String getCurrencyName(int currencyId) {
        return coreCurrencyRepository.getCurrencyName(currencyId);
    }
}