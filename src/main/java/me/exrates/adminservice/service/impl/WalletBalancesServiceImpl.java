package me.exrates.adminservice.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.repository.WalletBalancesRepository;
import me.exrates.adminservice.service.WalletBalancesService;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static me.exrates.adminservice.configurations.CacheConfiguration.ALL_MAIN_BALANCES_CACHE;
import static me.exrates.adminservice.util.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class WalletBalancesServiceImpl implements WalletBalancesService {

    private final WalletsApi walletsApi;
    private final WalletBalancesRepository walletBalancesRepository;
    private final Cache mainBalancesCache;

    @Autowired
    public WalletBalancesServiceImpl(WalletsApi walletsApi,
                                     WalletBalancesRepository walletBalancesRepository,
                                     @Qualifier(ALL_MAIN_BALANCES_CACHE) Cache mainBalancesCache) {
        this.walletsApi = walletsApi;
        this.walletBalancesRepository = walletBalancesRepository;
        this.mainBalancesCache = mainBalancesCache;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BalanceDto> getAllWalletBalances() {
        return walletBalancesRepository.getAllWalletBalances();
    }

    @Override
    public Map<String, BalanceDto> getCachedBalances() {
        return Objects.requireNonNull(mainBalancesCache.get(ALL_MAIN_BALANCES_CACHE, this::getAllWalletBalances)).stream()
                .collect(toMap(BalanceDto::getCurrencyName, Function.identity()));
    }

    @Override
    public void updateCurrencyBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency balances start...");

        final List<BalanceDto> balances = walletsApi.getBalancesFromApi();
        if (isEmpty(balances)) {
            return;
        }
        walletBalancesRepository.updateCurrencyWalletBalances(balances);
        log.info("Process of updating currency balances end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}