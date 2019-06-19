package me.exrates.adminservice.configurations;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.domain.UserInsight;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    public static final String ALL_RATES_CACHE = "all-rates-cache";
    public static final String ALL_MAIN_BALANCES_CACHE = "all-main-balances-cache";
    public static final String ALL_CURRENCIES_CACHE = "all-currencies-cache";
    public static final String ACTIVE_CURRENCIES_CACHE = "active-currencies-cache";
    public static final String CURRENCY_CACHE_BY_NAME = "currency-cache-name";
    public static final String CURRENCY_CACHE_BY_ID = "currency-cache-id";

    @Bean(ALL_RATES_CACHE)
    public Cache cacheAllRates() {
        return new CaffeineCache(ALL_RATES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(ALL_MAIN_BALANCES_CACHE)
    public Cache cacheAllMainBalances() {
        return new CaffeineCache(ALL_MAIN_BALANCES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(ALL_CURRENCIES_CACHE)
    public Cache cacheAllCurrencies() {
        return new CaffeineCache(ALL_CURRENCIES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(ACTIVE_CURRENCIES_CACHE)
    public Cache cacheActiveCurrencies() {
        return new CaffeineCache(ACTIVE_CURRENCIES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(CURRENCY_CACHE_BY_NAME)
    public Cache cacheCurrencyByName() {
        return new CaffeineCache(CURRENCY_CACHE_BY_NAME, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(CURRENCY_CACHE_BY_ID)
    public Cache cacheCurrencyById() {
        return new CaffeineCache(CURRENCY_CACHE_BY_ID, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }
}
