package me.exrates.adminservice.configurations;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    public static final String ALL_RATES_CACHE = "all-rates-cache";
    public static final String ALL_BALANCES_CACHE = "all-balances-cache";

    @Bean(ALL_RATES_CACHE)
    public Cache cacheAllRates() {
        return new CaffeineCache(ALL_RATES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(ALL_BALANCES_CACHE)
    public Cache cacheAllBalances() {
        return new CaffeineCache(ALL_BALANCES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }
}