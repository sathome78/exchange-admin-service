package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.domain.CoreCurrencyPairDto;
import me.exrates.adminservice.core.repository.CoreCurrencyRepository;
import me.exrates.adminservice.core.service.CoreCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static me.exrates.adminservice.configurations.CacheConfiguration.ACTIVE_CURRENCIES_CACHE;
import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_CURRENCIES_CACHE;
import static me.exrates.adminservice.configurations.CacheConfiguration.CURRENCY_CACHE_BY_ID;
import static me.exrates.adminservice.configurations.CacheConfiguration.CURRENCY_CACHE_BY_NAME;
import static me.exrates.adminservice.configurations.CacheConfiguration.CURRENCY_PAIR_CACHE_BY_ID;

@Log4j2
@Service
@Transactional
public class CoreCurrencyServiceImpl implements CoreCurrencyService {

    private final CoreCurrencyRepository coreCurrencyRepository;
    private final Cache currencyCacheByName;
    private final Cache currencyCacheById;
    private final Cache currencyPairCacheById;
    private final Cache allCurrenciesCache;
    private final Cache activeCurrenciesCache;

    @Autowired
    public CoreCurrencyServiceImpl(CoreCurrencyRepository coreCurrencyRepository,
                                   @Qualifier(CURRENCY_CACHE_BY_NAME) Cache currencyCacheByName,
                                   @Qualifier(CURRENCY_CACHE_BY_ID) Cache currencyCacheById,
                                   @Qualifier(CURRENCY_PAIR_CACHE_BY_ID) Cache currencyPairCacheById,
                                   @Qualifier(ALL_CURRENCIES_CACHE) Cache allCurrenciesCache,
                                   @Qualifier(ACTIVE_CURRENCIES_CACHE) Cache activeCurrenciesCache) {
        this.coreCurrencyRepository = coreCurrencyRepository;
        this.currencyCacheByName = currencyCacheByName;
        this.currencyCacheById = currencyCacheById;
        this.currencyPairCacheById = currencyPairCacheById;
        this.allCurrenciesCache = allCurrenciesCache;
        this.activeCurrenciesCache = activeCurrenciesCache;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCurrencyDto findCachedCurrencyById(int id) {
        return currencyCacheById.get(id, () -> coreCurrencyRepository.findCurrencyById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCurrencyDto findCachedCurrencyByName(String name) {
        return currencyCacheByName.get(name, () -> coreCurrencyRepository.findCurrencyByName(name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoreCurrencyDto> getCachedCurrencies() {
        return allCurrenciesCache.get(ALL_CURRENCIES_CACHE, coreCurrencyRepository::getAllCurrencies);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoreCurrencyDto> getCachedActiveCurrencies() {
        return activeCurrenciesCache.get(ACTIVE_CURRENCIES_CACHE, coreCurrencyRepository::getActiveCurrencies);
    }

    @Override
    public List<String> getActiveCurrencyNames() {
        return this.getCachedActiveCurrencies().stream()
                .map(CoreCurrencyDto::getName)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public String getCurrencyName(int currencyId) {
        return coreCurrencyRepository.getCurrencyName(currencyId);
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCurrencyPairDto findCachedCurrencyPairById(int id) {
        return currencyPairCacheById.get(id, () -> coreCurrencyRepository.findCurrencyPairById(id));
    }
}