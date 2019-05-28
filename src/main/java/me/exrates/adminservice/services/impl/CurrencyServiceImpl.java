package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.daos.CurrencyDao;
import me.exrates.adminservice.models.CurrencyDto;
import me.exrates.adminservice.services.CurrencyService;
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

    private final CurrencyDao currencyDao;
    private final Cache currencyCache;
    private final Cache allCurrenciesCache;

    @Autowired
    public CurrencyServiceImpl(CurrencyDao currencyDao,
                               @Qualifier(CURRENCY_CACHE) Cache currencyCache,
                               @Qualifier(ALL_CURRENCIES_CACHE) Cache allCurrenciesCache) {
        this.currencyDao = currencyDao;
        this.allCurrenciesCache = allCurrenciesCache;
        this.currencyCache = currencyCache;
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyDto findByName(String name) {
        return currencyCache.get(name, () -> currencyDao.findByName(name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyDto> getCachedCurrencies() {
        return allCurrenciesCache.get(ALL_CURRENCIES_CACHE, currencyDao::getAllCurrencies);
    }
}