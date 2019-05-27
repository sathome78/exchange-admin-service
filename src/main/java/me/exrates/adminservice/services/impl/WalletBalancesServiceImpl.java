package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.daos.WalletBalancesDao;
import me.exrates.adminservice.models.api.BalanceDto;
import me.exrates.adminservice.services.WalletBalancesService;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static me.exrates.adminservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class WalletBalancesServiceImpl implements WalletBalancesService {

    private final WalletsApi walletsApi;
    private final WalletBalancesDao walletBalancesDao;

    @Autowired
    public WalletBalancesServiceImpl(WalletsApi walletsApi,
                                     WalletBalancesDao walletBalancesDao) {
        this.walletsApi = walletsApi;
        this.walletBalancesDao = walletBalancesDao;
    }

    @Override
    public void updateCurrencyBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency balances start...");

        final List<BalanceDto> balances = walletsApi.getBalancesFromApi();
        if (isEmpty(balances)) {
            return;
        }
        balances.forEach(balanceDto -> {
            BalanceDto oldBalanceDto = walletBalancesDao.getBalancesByCurrencyName(balanceDto.getCurrencyName());
            if (isNull(oldBalanceDto)) {
                boolean inserted = walletBalancesDao.addCurrencyWalletBalances(balanceDto);
                log.debug("Process of add new wallet balances for currency: {} finished with result: {}", balanceDto.getCurrencyName(), inserted);
            } else {
                boolean updated = walletBalancesDao.updateCurrencyWalletBalances(balanceDto);
                log.debug("Process of update wallet balances for currency: {} finished with result: {}", balanceDto.getCurrencyName(), updated);
            }
        });
        log.info("Process of updating currency balances end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}