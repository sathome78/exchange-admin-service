package me.exrates.adminservice.core.service.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCurrencyRepository;
import me.exrates.adminservice.core.service.CoreCurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.exrates.adminservice.configurations.CacheConfiguration.ACTIVE_CURRENCIES_CACHE;
import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_CURRENCIES_CACHE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CoreCurrencyServiceImplTest.InnerConfig.class)
public class CoreCurrencyServiceImplTest {

    private static final String ALL_CURRENCIES_CACHE_TEST = "all-currencies-cache-test";
    private static final String ACTIVE_CURRENCIES_CACHE_TEST = "active-currencies-cache-test";
    private static final String CURRENCY_CACHE_BY_NAME_TEST = "currency-cache-name-test";
    private static final String CURRENCY_CACHE_BY_ID_TEST = "currency-cache-id-test";

    @Mock
    private CoreCurrencyRepository coreCurrencyRepository;
    @Autowired
    @Qualifier(CURRENCY_CACHE_BY_NAME_TEST)
    private Cache currencyCacheByName;
    @Autowired
    @Qualifier(CURRENCY_CACHE_BY_ID_TEST)
    private Cache currencyCacheById;
    @Autowired
    @Qualifier(ALL_CURRENCIES_CACHE_TEST)
    private Cache allCurrenciesCache;
    @Autowired
    @Qualifier(ACTIVE_CURRENCIES_CACHE_TEST)
    private Cache activeCurrenciesCache;

    private CoreCurrencyService coreCurrencyService;

    @Before
    public void setUp() throws Exception {
        currencyCacheByName.put("BTC", CoreCurrencyDto.builder().build());
        currencyCacheById.put(1, CoreCurrencyDto.builder().build());
        allCurrenciesCache.put(ALL_CURRENCIES_CACHE, Collections.singletonList(CoreCurrencyDto.builder().build()));
        activeCurrenciesCache.put(ACTIVE_CURRENCIES_CACHE, Collections.singletonList(CoreCurrencyDto.builder().build()));

        coreCurrencyService = spy(new CoreCurrencyServiceImpl(
                coreCurrencyRepository,
                currencyCacheByName,
                currencyCacheById,
                allCurrenciesCache,
                activeCurrenciesCache));
    }

    @Test
    public void findById_without_cache() {
        currencyCacheById.clear();

        doReturn(CoreCurrencyDto.builder().build())
                .when(coreCurrencyRepository)
                .findById(anyInt());

        CoreCurrencyDto currencyDto = coreCurrencyService.findById(1);

        assertNotNull(currencyDto);

        verify(coreCurrencyRepository, atLeastOnce()).findById(anyInt());
    }

    @Test
    public void findById_with_cache() {
        doReturn(CoreCurrencyDto.builder().build())
                .when(coreCurrencyRepository)
                .findById(anyInt());

        CoreCurrencyDto currencyDto = coreCurrencyService.findById(1);

        assertNotNull(currencyDto);

        verify(coreCurrencyRepository, never()).findById(anyInt());
    }

    @Test
    public void findByName_without_cache() {
        currencyCacheByName.clear();

        doReturn(CoreCurrencyDto.builder().build())
                .when(coreCurrencyRepository)
                .findByName(anyString());

        CoreCurrencyDto currencyDto = coreCurrencyService.findByName("BTC");

        assertNotNull(currencyDto);

        verify(coreCurrencyRepository, atLeastOnce()).findByName(anyString());
    }

    @Test
    public void findByName_with_cache() {
        doReturn(CoreCurrencyDto.builder().build())
                .when(coreCurrencyRepository)
                .findByName(anyString());

        CoreCurrencyDto currencyDto = coreCurrencyService.findByName("BTC");

        assertNotNull(currencyDto);

        verify(coreCurrencyRepository, never()).findByName(anyString());
    }

    @Test
    public void getCachedCurrencies_without_cache() {
        allCurrenciesCache.clear();

        doReturn(Collections.singletonList(CoreCurrencyDto.builder().build()))
                .when(coreCurrencyRepository)
                .getAllCurrencies();

        List<CoreCurrencyDto> cachedCurrencies = coreCurrencyService.getCachedCurrencies();

        assertNotNull(cachedCurrencies);
        assertFalse(cachedCurrencies.isEmpty());
        assertEquals(1, cachedCurrencies.size());

        verify(coreCurrencyRepository, atLeastOnce()).getAllCurrencies();
    }

    @Test
    public void getCachedCurrencies_with_cache() {
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().build()))
                .when(coreCurrencyRepository)
                .getAllCurrencies();

        List<CoreCurrencyDto> cachedCurrencies = coreCurrencyService.getCachedCurrencies();

        assertNotNull(cachedCurrencies);
        assertFalse(cachedCurrencies.isEmpty());
        assertEquals(1, cachedCurrencies.size());

        verify(coreCurrencyRepository, never()).getAllCurrencies();
    }

    @Test
    public void getActiveCachedCurrencies_without_cache() {
        activeCurrenciesCache.clear();

        doReturn(Collections.singletonList(CoreCurrencyDto.builder().build()))
                .when(coreCurrencyRepository)
                .getActiveCurrencies();

        List<CoreCurrencyDto> activeCachedCurrencies = coreCurrencyService.getActiveCachedCurrencies();

        assertNotNull(activeCachedCurrencies);
        assertFalse(activeCachedCurrencies.isEmpty());
        assertEquals(1, activeCachedCurrencies.size());

        verify(coreCurrencyRepository, atLeastOnce()).getActiveCurrencies();
    }

    @Test
    public void getActiveCachedCurrencies_with_cache() {
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().build()))
                .when(coreCurrencyRepository)
                .getActiveCurrencies();

        List<CoreCurrencyDto> activeCachedCurrencies = coreCurrencyService.getActiveCachedCurrencies();

        assertNotNull(activeCachedCurrencies);
        assertFalse(activeCachedCurrencies.isEmpty());
        assertEquals(1, activeCachedCurrencies.size());

        verify(coreCurrencyRepository, never()).getActiveCurrencies();
    }

    @Test
    public void getActiveCurrencyNames_list_is_empty() {
        activeCurrenciesCache.clear();

        doReturn(Collections.emptyList())
                .when(coreCurrencyRepository)
                .getActiveCurrencies();

        List<String> activeCurrencyNames = coreCurrencyService.getActiveCurrencyNames();

        assertNotNull(activeCurrencyNames);
        assertTrue(activeCurrencyNames.isEmpty());

        verify(coreCurrencyRepository, atLeastOnce()).getActiveCurrencies();
    }

    @Test
    public void getActiveCurrencyNames_list_is_not_empty() {
        activeCurrenciesCache.clear();

        doReturn(Collections.singletonList(CoreCurrencyDto.builder().name("BTC").build()))
                .when(coreCurrencyRepository)
                .getActiveCurrencies();

        List<String> activeCurrencyNames = coreCurrencyService.getActiveCurrencyNames();

        assertNotNull(activeCurrencyNames);
        assertFalse(activeCurrencyNames.isEmpty());
        assertEquals(1, activeCurrencyNames.size());

        verify(coreCurrencyRepository, atLeastOnce()).getActiveCurrencies();
    }

    @Test
    public void getCurrencyName_success() {
        doReturn("BTC")
                .when(coreCurrencyRepository)
                .getCurrencyName(anyInt());

        String currencyName = coreCurrencyService.getCurrencyName(1);

        assertNotNull(currencyName);
        assertEquals("BTC", currencyName);

        verify(coreCurrencyRepository, atLeastOnce()).getCurrencyName(anyInt());
    }

    @Configuration
    static class InnerConfig {

        @Bean(CURRENCY_CACHE_BY_NAME_TEST)
        public Cache cacheByName() {
            return new CaffeineCache(CURRENCY_CACHE_BY_NAME_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }

        @Bean(CURRENCY_CACHE_BY_ID_TEST)
        public Cache cacheById() {
            return new CaffeineCache(CURRENCY_CACHE_BY_ID_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }

        @Bean(ALL_CURRENCIES_CACHE_TEST)
        public Cache allCurrenciesCache() {
            return new CaffeineCache(ALL_CURRENCIES_CACHE_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }

        @Bean(ACTIVE_CURRENCIES_CACHE_TEST)
        public Cache activeCurrenciesCache() {
            return new CaffeineCache(ACTIVE_CURRENCIES_CACHE_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }
    }
}