package me.exrates.adminservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.api.ExchangeApi;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.ExchangeRatesRepository;
import me.exrates.adminservice.repository.WalletBalancesRepository;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.WalletBalancesService;
import me.exrates.adminservice.utils.ZipUtil;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_MAIN_BALANCES_CACHE;
import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_RATES_CACHE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExchangeRatesServiceImplTest.InnerConfig.class)
public class ExchangeRatesServiceImplTest {

    private static final String ALL_RATES_CACHE_TEST = "all-rates-cache-test";
    private static final String OBJECT_MAPPER_TEST = "json-mapper-test";

    @Mock
    private ExchangeApi exchangeApi;
    @Mock
    private ExchangeRatesRepository exchangeRatesRepository;
    @Autowired
    @Qualifier(ALL_RATES_CACHE_TEST)
    private Cache ratesCache;
    @Autowired
    @Qualifier(OBJECT_MAPPER_TEST)
    private ObjectMapper objectMapper;

    private ExchangeRatesService exchangeRatesService;

    private byte[] zippedBytes;

    @Before
    public void setUp() throws Exception {
        ratesCache.put(ALL_RATES_CACHE, Collections.singletonList(RateDto.builder().currencyName("BTC").build()));

        exchangeRatesService = spy(new ExchangeRatesServiceImpl(
                exchangeApi,
                exchangeRatesRepository,
                ratesCache,
                objectMapper));

        List<RateDto> ratesList = Collections.singletonList(RateDto.builder().build());
        byte[] bytes = objectMapper.writeValueAsBytes(ratesList);
        zippedBytes = ZipUtil.zip(bytes);
    }

    @Test
    public void getAllExchangeRates_success() {
        doReturn(Collections.singletonList(RateDto.builder().build()))
                .when(exchangeRatesRepository)
                .getAllExchangeRates();

        List<RateDto> allExchangeRates = exchangeRatesService.getAllExchangeRates();

        assertNotNull(allExchangeRates);
        assertFalse(allExchangeRates.isEmpty());
        assertEquals(1, allExchangeRates.size());

        verify(exchangeRatesRepository, atLeastOnce()).getAllExchangeRates();
    }

    @Test
    public void getCachedRates_without_cache() {
        ratesCache.clear();

        doReturn(Collections.singletonList(RateDto.builder()
                .currencyName("BTC")
                .build()))
                .when(exchangeRatesRepository)
                .getAllExchangeRates();

        Map<String, RateDto> cachedRates = exchangeRatesService.getCachedRates();

        assertNotNull(cachedRates);
        assertFalse(cachedRates.isEmpty());
        assertEquals(1, cachedRates.size());

        verify(exchangeRatesRepository, atLeastOnce()).getAllExchangeRates();
    }

    @Test
    public void getCachedRates_with_cache() {
        doReturn(Collections.singletonList(RateDto.builder()
                .currencyName("BTC")
                .build()))
                .when(exchangeRatesRepository)
                .getAllExchangeRates();

        Map<String, RateDto> cachedRates = exchangeRatesService.getCachedRates();

        assertNotNull(cachedRates);
        assertFalse(cachedRates.isEmpty());
        assertEquals(1, cachedRates.size());

        verify(exchangeRatesRepository, never()).getAllExchangeRates();
    }

    @Test
    public void updateCurrencyExchangeRates_rates_list_is_empty() {
        doReturn(Collections.emptyList())
                .when(exchangeApi)
                .getRatesFromApi();

        exchangeRatesService.updateCurrencyExchangeRates();

        verify(exchangeApi, atLeastOnce()).getRatesFromApi();
        verify(exchangeRatesRepository, never()).updateCurrencyExchangeRates(anyList());
    }

    @Test
    public void updateCurrencyExchangeRates_rates_list_is_not_empty() {
        doReturn(Collections.singletonList(RateDto.builder().currencyName("BTC").build()))
                .when(exchangeApi)
                .getRatesFromApi();

        exchangeRatesService.updateCurrencyExchangeRates();

        verify(exchangeApi, atLeastOnce()).getRatesFromApi();
        verify(exchangeRatesRepository, atLeastOnce()).updateCurrencyExchangeRates(anyList());
    }

    @Test
    public void updateCurrencyExchangeRateHistory_success() {
        doReturn(Collections.singletonList(RateDto.builder().build()))
                .when(exchangeRatesRepository)
                .getAllExchangeRates();
        doNothing()
                .when(exchangeRatesRepository)
                .saveCurrencyExchangeRatesHistory(any());

        exchangeRatesService.updateCurrencyExchangeRateHistory();

        verify(exchangeRatesRepository, atLeastOnce()).getAllExchangeRates();
        verify(exchangeRatesRepository, atLeastOnce()).saveCurrencyExchangeRatesHistory(any());
    }

    @Test
    public void getExchangeRatesHistoryByDate_list_is_empty() {
        final LocalDate now = LocalDate.now();

        doReturn(Collections.emptyList())
                .when(exchangeRatesRepository)
                .getExchangeRatesHistoryByDate(any(LocalDateTime.class), any(LocalDateTime.class));

        List<RateHistoryDto> exchangeRatesHistoryList = exchangeRatesService.getExchangeRatesHistoryByDate(now);

        assertNotNull(exchangeRatesHistoryList);
        assertTrue(exchangeRatesHistoryList.isEmpty());

        verify(exchangeRatesRepository, atLeastOnce()).getExchangeRatesHistoryByDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void getExchangeRatesHistoryByDate_list_is_not_empty() {
        final LocalDate now = LocalDate.now();

        doReturn(Collections.singletonList(RateHistoryDto.builder()
                .content(zippedBytes)
                .build()))
                .when(exchangeRatesRepository)
                .getExchangeRatesHistoryByDate(any(LocalDateTime.class), any(LocalDateTime.class));

        List<RateHistoryDto> exchangeRatesHistoryList = exchangeRatesService.getExchangeRatesHistoryByDate(now);

        assertNotNull(exchangeRatesHistoryList);
        assertFalse(exchangeRatesHistoryList.isEmpty());
        assertEquals(1, exchangeRatesHistoryList.size());

        verify(exchangeRatesRepository, atLeastOnce()).getExchangeRatesHistoryByDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Configuration
    static class InnerConfig {

        @Bean(ALL_RATES_CACHE_TEST)
        public Cache cache() {
            return new CaffeineCache(ALL_RATES_CACHE_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }

        @Bean(OBJECT_MAPPER_TEST)
        public ObjectMapper mapper() {
            return new ObjectMapper()
                    .findAndRegisterModules()
                    .registerModule(new JavaTimeModule());
        }
    }
}