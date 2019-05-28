package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.daos.WalletDao;
import me.exrates.adminservice.models.CurrencyDto;
import me.exrates.adminservice.models.ExternalWalletBalancesDto;
import me.exrates.adminservice.models.InternalWalletBalancesDto;
import me.exrates.adminservice.models.api.BalanceDto;
import me.exrates.adminservice.models.api.RateDto;
import me.exrates.adminservice.services.CurrencyService;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.WalletBalancesService;
import me.exrates.adminservice.services.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Log4j2
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private final WalletDao walletDao;
    private final ExchangeRatesService exchangeRatesService;
    private final WalletBalancesService walletBalancesService;
    private final CurrencyService currencyService;
    private final WalletsApi walletsApi;

    @Autowired
    public WalletServiceImpl(WalletDao walletDao,
                             ExchangeRatesService exchangeRatesService,
                             WalletBalancesService walletBalancesService,
                             CurrencyService currencyService,
                             WalletsApi walletsApi) {
        this.walletDao = walletDao;
        this.exchangeRatesService = exchangeRatesService;
        this.walletBalancesService = walletBalancesService;
        this.currencyService = currencyService;
        this.walletsApi = walletsApi;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExternalWalletBalancesDto> getExternalWalletBalances() {
        final Map<String, CurrencyDto> currenciesMap = currencyService.getCachedCurrencies()
                .stream()
                .collect(toMap(
                        CurrencyDto::getName,
                        Function.identity()
                ));

        return walletDao.getExternalMainWalletBalances()
                .stream()
                .map(exWallet -> {
                    CurrencyDto currencyDto = currenciesMap.get(exWallet.getCurrencyName());

                    if (currencyDto.isHidden()) {
                        return ExternalWalletBalancesDto.getZeroBalances(exWallet.getCurrencyId(), exWallet.getCurrencyName());
                    }
                    return exWallet;
                })
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        return walletDao.getInternalWalletBalances();
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        return walletDao.getWalletBalances();
    }

    @Override
    public void updateExternalMainWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external main wallets start...");

        final Map<String, RateDto> rates = exchangeRatesService.getCachedRates();
        final Map<String, BalanceDto> balances = walletBalancesService.getCachedBalances();

        final List<ExternalWalletBalancesDto> externalWalletBalances = this.getExternalWalletBalances();

        if (rates.isEmpty() || balances.isEmpty() || externalWalletBalances.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (ExternalWalletBalancesDto exWallet : externalWalletBalances) {
            final String currencyName = exWallet.getCurrencyName();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
            BalanceDto balanceDto = balances.getOrDefault(currencyName, BalanceDto.zeroBalance(currencyName));

            BigDecimal usdRate = rateDto.getUsdRate();
            BigDecimal btcRate = rateDto.getBtcRate();

            BigDecimal mainBalance = balanceDto.getBalance();
            LocalDateTime lastBalanceUpdate = balanceDto.getLastUpdatedAt();

            ExternalWalletBalancesDto.Builder builder = exWallet.toBuilder()
                    .usdRate(usdRate)
                    .btcRate(btcRate)
                    .mainBalance(mainBalance);

            if (nonNull(lastBalanceUpdate)) {
                builder.lastUpdatedDate(lastBalanceUpdate);
            }
            exWallet = builder.build();
            walletDao.updateExternalMainWalletBalances(exWallet);
        }
        log.info("Process of updating external main wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateExternalReservedWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external reserved wallets start...");

        final Map<String, BigDecimal> reservedBalances = walletsApi.getReservedBalancesFromApi();

        if (reservedBalances.isEmpty()) {
            log.info("Wallet api did not return any data");
            return;
        }

        for (Map.Entry<String, BigDecimal> entry : reservedBalances.entrySet()) {
            final String compositeKey = entry.getKey();
            final BigDecimal balance = entry.getValue();

            String[] data = compositeKey.split("\\|\\|");
            final String currencySymbol = data[0];
            final String walletAddress = data[1];
            final LocalDateTime lastReservedBalanceUpdate = StringUtils.isNotEmpty(data[3])
                    ? LocalDateTime.parse(data[3], FORMATTER)
                    : null;

            CurrencyDto currency = currencyService.findByName(currencySymbol);
            if (isNull(currency)) {
                return;
            }

            walletDao.updateExternalReservedWalletBalances(currency.getId(), walletAddress, balance, lastReservedBalanceUpdate);
        }
        log.info("Process of updating external reserved wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateInternalWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating internal wallets start...");

        final Map<String, RateDto> rates = exchangeRatesService.getCachedRates();

        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalancesMap = this.getWalletBalances()
                .stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        if (rates.isEmpty() || internalWalletBalancesMap.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (Map.Entry<String, List<InternalWalletBalancesDto>> entry : internalWalletBalancesMap.entrySet()) {
            final String currencyName = entry.getKey();
            final List<InternalWalletBalancesDto> balancesByRoles = entry.getValue();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));

            final BigDecimal usdRate = rateDto.getUsdRate();
            final BigDecimal btcRate = rateDto.getBtcRate();

            for (InternalWalletBalancesDto inWallet : balancesByRoles) {
                inWallet = inWallet.toBuilder()
                        .usdRate(usdRate)
                        .btcRate(btcRate)
                        .build();
                walletDao.updateInternalWalletBalances(inWallet);
            }
        }
        log.info("Process of updating internal wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}