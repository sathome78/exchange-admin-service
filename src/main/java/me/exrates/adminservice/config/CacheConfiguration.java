package me.exrates.adminservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    public static final String ALL_RATES_CACHE = "all-rates-cache";
    public static final String ALL_MAIN_BALANCES_CACHE = "all-main-balances-cache";
    public static final String ALL_CURRENCIES_CACHE = "all-currencies-cache";
    public static final String CURRENCY_CACHE = "currency-cache";

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

    @Bean(CURRENCY_CACHE)
    public Cache cacheCurrency() {
        return new CaffeineCache(CURRENCY_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }
}
