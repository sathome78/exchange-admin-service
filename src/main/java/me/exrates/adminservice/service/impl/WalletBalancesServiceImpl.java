package me.exrates.adminservice.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.repository.WalletBalancesDao;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.service.WalletBalancesService;
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

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static me.exrates.adminservice.config.CacheConfiguration.ALL_BALANCES_CACHE;
import static me.exrates.adminservice.util.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class WalletBalancesServiceImpl implements WalletBalancesService {

    private final WalletsApi walletsApi;
    private final WalletBalancesDao walletBalancesDao;
    private final Cache balancesCache;

    @Autowired
    public WalletBalancesServiceImpl(WalletsApi walletsApi,
                                     WalletBalancesDao walletBalancesDao,
                                     @Qualifier(ALL_BALANCES_CACHE) Cache balancesCache) {
        this.walletsApi = walletsApi;
        this.walletBalancesDao = walletBalancesDao;
        this.balancesCache = balancesCache;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BalanceDto> getAllWalletBalances() {
        return walletBalancesDao.getAllWalletBalances();
    }

    @Override
    public Map<String, BalanceDto> getCachedBalances() {
        return Objects.requireNonNull(balancesCache.get(ALL_BALANCES_CACHE, this::getAllWalletBalances)).stream()
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
        for (BalanceDto balanceDto : balances) {
            BalanceDto oldBalanceDto = walletBalancesDao.getBalancesByCurrencyName(balanceDto.getCurrencyName());
            if (isNull(oldBalanceDto)) {
                boolean inserted = walletBalancesDao.addCurrencyWalletBalances(balanceDto);
                log.debug("Process of add new wallet balances for currency: {} finished with result: {}", balanceDto.getCurrencyName(), inserted);
            } else {
                if (oldBalanceDto.getBalance().compareTo(balanceDto.getBalance()) == 0) {
                    continue;
                }
                boolean updated = walletBalancesDao.updateCurrencyWalletBalances(balanceDto);
                log.debug("Process of update wallet balances for currency: {} finished with result: {}", balanceDto.getCurrencyName(), updated);
            }
        }
        log.info("Process of updating currency balances end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}
