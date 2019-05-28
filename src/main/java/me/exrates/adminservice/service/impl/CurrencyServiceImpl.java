package me.exrates.adminservice.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCurrencyRepository;
import me.exrates.adminservice.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_CURRENCIES_CACHE;
import static me.exrates.adminservice.configurations.CacheConfiguration.CURRENCY_CACHE;

@Log4j2
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CoreCurrencyRepository coreCurrencyRepository;
    private final Cache currencyCache;
    private final Cache allCurrenciesCache;

    @Autowired
    public CurrencyServiceImpl(CoreCurrencyRepository coreCurrencyRepository,
                               @Qualifier(CURRENCY_CACHE) Cache currencyCache,
                               @Qualifier(ALL_CURRENCIES_CACHE) Cache allCurrenciesCache) {
        this.coreCurrencyRepository = coreCurrencyRepository;
        this.allCurrenciesCache = allCurrenciesCache;
        this.currencyCache = currencyCache;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCurrencyDto findByName(String name) {
        return currencyCache.get(name, () -> coreCurrencyRepository.findByName(name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoreCurrencyDto> getCachedCurrencies() {
        return allCurrenciesCache.get(ALL_CURRENCIES_CACHE, coreCurrencyRepository::getAllCurrencies);
    }
}