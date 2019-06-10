package me.exrates.adminservice.services.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.repository.WalletBalancesRepository;
import me.exrates.adminservice.services.WalletBalancesService;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_MAIN_BALANCES_CACHE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WalletBalancesServiceImplTest.InnerConfig.class)
public class WalletBalancesServiceImplTest {

    private static final String ALL_MAIN_BALANCES_CACHE_TEST = "all-main-balances-cache-test";

    @Mock
    private WalletsApi walletsApi;
    @Mock
    private WalletBalancesRepository walletBalancesRepository;
    @Autowired
    @Qualifier(ALL_MAIN_BALANCES_CACHE_TEST)
    private Cache mainBalancesCache;

    private WalletBalancesService walletBalancesService;

    @Before
    public void setUp() throws Exception {
        mainBalancesCache.put(ALL_MAIN_BALANCES_CACHE, Collections.singletonList(BalanceDto.builder().currencyName("BTC").build()));

        walletBalancesService = spy(new WalletBalancesServiceImpl(
                walletsApi,
                walletBalancesRepository,
                mainBalancesCache));
    }

    @Test
    public void getAllWalletBalances_success() {
        doReturn(Collections.singletonList(BalanceDto.builder().build()))
                .when(walletBalancesRepository)
                .getAllWalletBalances();

        List<BalanceDto> allWalletBalances = walletBalancesService.getAllWalletBalances();

        assertNotNull(allWalletBalances);
        assertFalse(allWalletBalances.isEmpty());
        assertEquals(1, allWalletBalances.size());

        verify(walletBalancesRepository, atLeastOnce()).getAllWalletBalances();
    }

    @Test
    public void getCachedBalances_without_cache() {
        mainBalancesCache.clear();

        doReturn(Collections.singletonList(BalanceDto.builder()
                .currencyName("BTC")
                .build()))
                .when(walletBalancesRepository)
                .getAllWalletBalances();

        Map<String, BalanceDto> cachedBalances = walletBalancesService.getCachedBalances();

        assertNotNull(cachedBalances);
        assertFalse(cachedBalances.isEmpty());
        assertEquals(1, cachedBalances.size());

        verify(walletBalancesRepository, atLeastOnce()).getAllWalletBalances();
    }

    @Test
    public void getCachedBalances_with_cache() {
        doReturn(Collections.singletonList(BalanceDto.builder()
                .currencyName("BTC")
                .build()))
                .when(walletBalancesRepository)
                .getAllWalletBalances();

        Map<String, BalanceDto> cachedBalances = walletBalancesService.getCachedBalances();

        assertNotNull(cachedBalances);
        assertFalse(cachedBalances.isEmpty());
        assertEquals(1, cachedBalances.size());

        verify(walletBalancesRepository, never()).getAllWalletBalances();
    }

    @Test
    public void updateCurrencyBalances_balances_list_is_empty() {
        doReturn(Collections.emptyList())
                .when(walletsApi)
                .getBalancesFromApi();

        walletBalancesService.updateCurrencyBalances();

        verify(walletsApi, atLeastOnce()).getBalancesFromApi();
        verify(walletBalancesRepository, never()).updateCurrencyWalletBalances(anyList());
    }

    @Test
    public void updateCurrencyBalances_balances_list_is_not_empty() {
        doReturn(Collections.singletonList(BalanceDto.builder().build()))
                .when(walletsApi)
                .getBalancesFromApi();

        walletBalancesService.updateCurrencyBalances();

        verify(walletsApi, atLeastOnce()).getBalancesFromApi();
        verify(walletBalancesRepository, atLeastOnce()).updateCurrencyWalletBalances(anyList());
    }

    @Configuration
    static class InnerConfig {

        @Bean(ALL_MAIN_BALANCES_CACHE_TEST)
        public Cache cache() {
            return new CaffeineCache(ALL_MAIN_BALANCES_CACHE_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }
    }
}