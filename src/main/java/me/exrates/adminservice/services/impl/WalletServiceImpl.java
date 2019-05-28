package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.repository.WalletRepository;
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
import java.util.Collections;
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

    private final WalletRepository walletRepository;
    private final CoreWalletRepository coreWalletRepository;
    private final ExchangeRatesService exchangeRatesService;
    private final WalletBalancesService walletBalancesService;
    private final CurrencyService currencyService;
    private final WalletsApi walletsApi;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             CoreWalletRepository coreWalletRepository,
                             ExchangeRatesService exchangeRatesService,
                             WalletBalancesService walletBalancesService,
                             CurrencyService currencyService,
                             WalletsApi walletsApi) {
        this.walletRepository = walletRepository;
        this.coreWalletRepository = coreWalletRepository;
        this.exchangeRatesService = exchangeRatesService;
        this.walletBalancesService = walletBalancesService;
        this.currencyService = currencyService;
        this.walletsApi = walletsApi;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExternalWalletBalancesDto> getExternalWalletBalances() {
        final Map<String, CoreCurrencyDto> currenciesMap = currencyService.getCachedCurrencies()
                .stream()
                .collect(toMap(
                        CoreCurrencyDto::getName,
                        Function.identity()
                ));

        return walletRepository.getExternalMainWalletBalances()
                .stream()
                .map(exWallet -> {
                    CoreCurrencyDto currencyDto = currenciesMap.get(exWallet.getCurrencyName());

                    if (nonNull(currencyDto) && currencyDto.isHidden()) {
                        return ExternalWalletBalancesDto.getZeroBalances(exWallet.getCurrencyId(), exWallet.getCurrencyName());
                    }
                    return exWallet;
                })
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        final Map<String, CoreCurrencyDto> currenciesMap = currencyService.getCachedCurrencies()
                .stream()
                .collect(toMap(
                        CoreCurrencyDto::getName,
                        Function.identity()
                ));

        return walletRepository.getInternalWalletBalances()
                .stream()
                .map(inWallet -> {
                    CoreCurrencyDto currencyDto = currenciesMap.get(inWallet.getCurrencyName());

                    if (nonNull(currencyDto) && currencyDto.isHidden()) {
                        return InternalWalletBalancesDto.getZeroBalances(inWallet.getCurrencyId(), inWallet.getCurrencyName(), inWallet.getRoleId(), inWallet.getRoleName());
                    }
                    return inWallet;
                })
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        return coreWalletRepository.getWalletBalances();
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

            exWallet.setUsdRate(usdRate);
            exWallet.setBtcRate(btcRate);
            exWallet.setMainBalance(mainBalance);

            if (nonNull(lastBalanceUpdate)) {
                exWallet.setLastUpdatedDate(lastBalanceUpdate);
            }
        }
        walletRepository.updateExternalMainWalletBalances(externalWalletBalances);
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

            CoreCurrencyDto currency = currencyService.findByName(currencySymbol);
            if (isNull(currency)) {
                return;
            }

            walletRepository.updateExternalReservedWalletBalances(currency.getId(), walletAddress, balance, lastReservedBalanceUpdate);
        }
        log.info("Process of updating external reserved wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateInternalWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating internal wallets start...");

        final Map<String, RateDto> rates = exchangeRatesService.getCachedRates();
        final Map<String, List<InternalWalletBalancesDto>> balances = this.getWalletBalances()
                .stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        final List<InternalWalletBalancesDto> internalWalletBalances = this.getInternalWalletBalances();

        if (rates.isEmpty() || balances.isEmpty() || internalWalletBalances.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (InternalWalletBalancesDto inWallet : internalWalletBalances) {
            final Integer currencyId = inWallet.getCurrencyId();
            final String currencyName = inWallet.getCurrencyName();
            final Integer roleId = inWallet.getRoleId();
            final UserRole roleName = inWallet.getRoleName();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
            List<InternalWalletBalancesDto> balancesByRoles = balances.getOrDefault(currencyName, Collections.emptyList());

            BigDecimal usdRate = rateDto.getUsdRate();
            BigDecimal btcRate = rateDto.getBtcRate();

            final Map<UserRole, InternalWalletBalancesDto> byRolesMap = balancesByRoles
                    .stream()
                    .collect(toMap(
                            InternalWalletBalancesDto::getRoleName,
                            Function.identity()
                    ));
            final InternalWalletBalancesDto balanceByRole = byRolesMap.getOrDefault(roleName, InternalWalletBalancesDto.getZeroBalances(currencyId, currencyName, roleId, roleName));

            BigDecimal totalBalance = balanceByRole.getTotalBalance();

            inWallet.setUsdRate(usdRate);
            inWallet.setBtcRate(btcRate);
            inWallet.setTotalBalance(totalBalance);
        }
        walletRepository.updateInternalWalletBalances(internalWalletBalances);
        log.info("Process of updating internal wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}