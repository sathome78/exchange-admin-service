package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.domain.BalancesDto;
import me.exrates.adminservice.domain.DashboardOneDto;
import me.exrates.adminservice.domain.DashboardTwoDto;
import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.domain.enums.DeviationStatus;
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
import java.util.Objects;
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

    @Override
    public PagedResult<ExternalWalletBalancesDto> getExternalWalletBalances(Integer limit, Integer offset) {
        final List<ExternalWalletBalancesDto> externalWalletBalances = this.getExternalWalletBalances();

        int recordsCount = externalWalletBalances.size();

        List<ExternalWalletBalancesDto> items = Collections.emptyList();
        if (recordsCount > 0) {
            items = externalWalletBalances.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(toList());
        }
        PagedResult<ExternalWalletBalancesDto> pagedResult = new PagedResult<>();
        pagedResult.setCount(recordsCount);
        pagedResult.setItems(items);
        return pagedResult;
    }

    @Override
    public DashboardOneDto getDashboardOne() {
        final BigDecimal exWalletSum = this.getExternalWalletBalances().stream()
                .map(ExternalWalletBalancesDto::getTotalBalanceUSD)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal inWalletSum = this.getInternalWalletBalances().stream()
                .filter(inWallet -> inWallet.getRoleName() != UserRole.BOT_TRADER)
                .filter(inWallet -> inWallet.getRoleName() != UserRole.OUTER_MARKET_BOT)
                .map(InternalWalletBalancesDto::getTotalBalanceUSD)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal deviation = exWalletSum.subtract(inWalletSum);

        final int allCurrenciesCount = currencyService.getCachedCurrencies().size();

        final int activeCurrenciesCount = currencyService.getActiveCachedCurrencies().size();

        return DashboardOneDto.builder()
                .exWalletBalancesUSDSum(exWalletSum)
                .inWalletBalancesUSDSum(inWalletSum)
                .deviationUSD(deviation)
                .allCurrenciesCount(allCurrenciesCount)
                .activeCurrenciesCount(activeCurrenciesCount)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExternalWalletBalancesDto> getExternalWalletBalances() {
        return walletRepository.getExternalMainWalletBalances()
                .stream()
                .map(exWallet -> {
                    CoreCurrencyDto currencyDto = getActiveCurrenciesMap().get(exWallet.getCurrencyName());

                    if (isNull(currencyDto)) {
                        return null;
                    }
                    return exWallet;
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        return walletRepository.getInternalWalletBalances()
                .stream()
                .map(inWallet -> {
                    CoreCurrencyDto currencyDto = getActiveCurrenciesMap().get(inWallet.getCurrencyName());

                    if (isNull(currencyDto)) {
                        return null;
                    }
                    return inWallet;
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Map<String, CoreCurrencyDto> getActiveCurrenciesMap() {
        return currencyService.getActiveCachedCurrencies().stream()
                .collect(toMap(
                        CoreCurrencyDto::getName,
                        Function.identity()
                ));
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

    @Override
    public void createWalletAddress(int currencyId) {
        walletRepository.createReservedWalletAddress(currencyId);
    }

    @Override
    public void deleteWalletAddress(int id, int currencyId, String walletAddress) {
        walletRepository.deleteReservedWalletAddress(id, currencyId);

        final String currencySymbol = currencyService.getCurrencyName(currencyId);
        if (isNull(currencySymbol)) {
            log.info("External reserved address have not been deleted from WalletChecker Service");
            return;
        }

        boolean deleted = walletsApi.deleteReservedWallet(currencySymbol, walletAddress);

        log.info("External reserved address [{}:{}] {}",
                currencySymbol,
                walletAddress,
                deleted ? "have been deleted" : "have not been deleted");
    }

    @Override
    public void updateWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto, boolean isSavedAsAddress) {
        walletRepository.updateReservedWalletAddress(externalReservedWalletAddressDto);

        if (isSavedAsAddress) {
            final int currencyId = externalReservedWalletAddressDto.getCurrencyId();
            final String walletAddress = externalReservedWalletAddressDto.getWalletAddress();

            final String currencySymbol = currencyService.getCurrencyName(currencyId);
            if (isNull(currencySymbol)) {
                log.info("External reserved address have not been saved in WalletChecker Service");
                return;
            }

            boolean saved = walletsApi.addReservedWallet(currencySymbol, walletAddress);

            log.info("External reserved address [{}:{}] {}",
                    currencySymbol,
                    walletAddress,
                    saved ? "have been saved" : "have not been saved");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId) {
        return walletRepository.getReservedWalletsByCurrencyId(currencyId);
    }

    @Override
    public BigDecimal getExternalReservedWalletBalance(Integer currencyId, String walletAddress) {
        CoreCurrencyDto currency = currencyService.findById(currencyId);
        if (isNull(currency)) {
            log.info("Currency with id: {} not found", currencyId);
            return null;
        }
        return walletsApi.getBalanceByCurrencyAndWallet(currency.getName(), walletAddress);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal retrieveSummaryUSD() {
        return walletRepository.retrieveSummaryUSD();
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal retrieveSummaryBTC() {
        return walletRepository.retrieveSummaryBTC();
    }

    @Override
    public void updateAccountingImbalance(String currencyName, BigDecimal accountingProfit, BigDecimal accountingManualBalanceChanges) {
        walletRepository.updateAccountingImbalance(currencyName, accountingProfit, accountingManualBalanceChanges);
    }

    @Override
    public void updateSignOfMonitoringForCurrency(int currencyId, boolean signOfMonitoring) {
        walletRepository.updateSignOfMonitoringForCurrency(currencyId, signOfMonitoring);
    }

    @Override
    public void updateMonitoringRangeForCurrency(int currencyId, BigDecimal coinRange, boolean checkByCoinRange, BigDecimal usdRange, boolean checkByUsdRange) {
        walletRepository.updateMonitoringRangeForCurrency(currencyId, coinRange, checkByCoinRange, usdRange, checkByUsdRange);
    }

    @Override
    public PagedResult<BalancesDto> getBalancesSliceStatistic(List<String> currencyNames, BigDecimal minExBalance, BigDecimal maxExBalance,
                                                              BigDecimal minInBalance, BigDecimal maxInBalance, Integer limit, Integer offset) {
        final List<BalancesDto> balancesSliceStatistic = this.getBalancesSliceStatistic(currencyNames, minExBalance, maxExBalance, minInBalance, maxInBalance);

        int recordsCount = balancesSliceStatistic.size();

        List<BalancesDto> items = Collections.emptyList();
        if (recordsCount > 0) {
            items = balancesSliceStatistic.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(toList());
        }
        PagedResult<BalancesDto> pagedResult = new PagedResult<>();
        pagedResult.setCount(recordsCount);
        pagedResult.setItems(items);
        return pagedResult;
    }

    @Override
    public DashboardTwoDto getDashboardTwo(List<String> currencyNames, BigDecimal minExBalance, BigDecimal maxExBalance,
                                           BigDecimal minInBalance, BigDecimal maxInBalance) {
        List<BalancesDto> balancesSliceStatistic = this.getBalancesSliceStatistic(currencyNames, minExBalance, maxExBalance, minInBalance, maxInBalance);

        final BigDecimal exWalletBalancesUSDSum = balancesSliceStatistic.stream()
                .map(BalancesDto::getTotalWalletBalanceUSD)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal inWalletBalancesUSDSum = balancesSliceStatistic.stream()
                .map(BalancesDto::getTotalExratesBalanceUSD)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal deviationUSD = balancesSliceStatistic.stream()
                .map(BalancesDto::getDeviationUSD)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal exWalletBalancesBTCSum = balancesSliceStatistic.stream()
                .map(BalancesDto::getTotalWalletBalanceBTC)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal inWalletBalancesBTCSum = balancesSliceStatistic.stream()
                .map(BalancesDto::getTotalExratesBalanceBTC)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final BigDecimal deviationBTC = balancesSliceStatistic.stream()
                .map(BalancesDto::getDeviationBTC)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        final int lowDeviationCount = (int) balancesSliceStatistic.stream()
                .filter(statistic -> statistic.getDeviationStatus() == DeviationStatus.RED)
                .count();

        final int highDeviationCount = (int) balancesSliceStatistic.stream()
                .filter(statistic -> statistic.getDeviationStatus() == DeviationStatus.GREEN)
                .count();

        final int normalDeviationCount = (int) balancesSliceStatistic.stream()
                .filter(statistic -> statistic.getDeviationStatus() == DeviationStatus.YELLOW)
                .count();

        final int monitoredCurrenciesCount = lowDeviationCount + highDeviationCount + normalDeviationCount;

        final int activeCurrenciesCount = currencyService.getActiveCachedCurrencies().size();

        return DashboardTwoDto.builder()
                .exWalletBalancesUSDSum(exWalletBalancesUSDSum)
                .inWalletBalancesUSDSum(inWalletBalancesUSDSum)
                .deviationUSD(deviationUSD)
                .exWalletBalancesBTCSum(exWalletBalancesBTCSum)
                .inWalletBalancesBTCSum(inWalletBalancesBTCSum)
                .deviationBTC(deviationBTC)
                .redDeviationCount(lowDeviationCount)
                .greenDeviationCount(highDeviationCount)
                .yellowDeviationCount(normalDeviationCount)
                .activeCurrenciesCount(activeCurrenciesCount)
                .monitoredCurrenciesCount(monitoredCurrenciesCount)
                .build();
    }

    @Override
    public List<BalancesDto> getBalancesSliceStatistic(List<String> currencyNames, BigDecimal minExBalance, BigDecimal maxExBalance,
                                                       BigDecimal minInBalance, BigDecimal maxInBalance) {
        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalances = this.getInternalWalletBalances().stream()
                .filter(inWallet -> inWallet.getRoleName() != UserRole.BOT_TRADER)
                .filter(inWallet -> inWallet.getRoleName() != UserRole.OUTER_MARKET_BOT)
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        return this.getExternalWalletBalances().stream()
                .map(extWalletBalance -> {
                    final Integer currencyId = extWalletBalance.getCurrencyId();
                    final String currencyName = extWalletBalance.getCurrencyName();
                    final BigDecimal usdRate = extWalletBalance.getUsdRate();
                    final BigDecimal btcRate = extWalletBalance.getBtcRate();

                    List<InternalWalletBalancesDto> intWalletBalances = internalWalletBalances.getOrDefault(currencyName, Collections.emptyList());

                    final BigDecimal externalTotalBalance = extWalletBalance.getTotalBalance();
                    final BigDecimal externalTotalBalanceUSD = extWalletBalance.getTotalBalanceUSD();
                    final BigDecimal externalTotalBalanceBTC = extWalletBalance.getTotalBalanceBTC();

                    final BigDecimal internalTotalBalance = intWalletBalances.stream()
                            .map(InternalWalletBalancesDto::getTotalBalance)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                    final BigDecimal internalTotalBalanceUSD = intWalletBalances.stream()
                            .map(InternalWalletBalancesDto::getTotalBalanceUSD)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                    final BigDecimal internalTotalBalanceBTC = intWalletBalances.stream()
                            .map(InternalWalletBalancesDto::getTotalBalanceBTC)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                    final BigDecimal deviation = externalTotalBalance.subtract(internalTotalBalance);
                    final BigDecimal deviationUSD = externalTotalBalanceUSD.subtract(internalTotalBalanceUSD);
                    final BigDecimal deviationBTC = externalTotalBalanceBTC.subtract(internalTotalBalanceBTC);

                    if ((nonNull(currencyNames) && !currencyNames.contains(currencyName))
                            || (nonNull(minExBalance) && externalTotalBalanceUSD.compareTo(minExBalance) < 0)
                            || (nonNull(maxExBalance) && externalTotalBalanceUSD.compareTo(maxExBalance) > 0)
                            || (nonNull(minInBalance) && internalTotalBalanceUSD.compareTo(minInBalance) < 0)
                            || (nonNull(maxInBalance) && internalTotalBalanceUSD.compareTo(maxInBalance) > 0)) {
                        return null;
                    }

                    final boolean signOfMonitoring = extWalletBalance.isSignOfMonitoring();
                    final BigDecimal coinRange = extWalletBalance.getCoinRange();
                    final boolean checkCoinRange = extWalletBalance.isCheckCoinRange();
                    final BigDecimal usdRange = extWalletBalance.getUsdRange();
                    final boolean checkUsdRange = extWalletBalance.isCheckUsdRange();

                    DeviationStatus deviationStatus;
                    if (signOfMonitoring) {
                        if (checkCoinRange) {
                            deviationStatus = deviation.compareTo(coinRange.negate()) >= 0 && deviation.compareTo(coinRange) <= 0
                                    ? DeviationStatus.YELLOW
                                    : deviation.compareTo(coinRange.negate()) < 0
                                    ? DeviationStatus.RED
                                    : DeviationStatus.GREEN;
                        } else if (checkUsdRange) {
                            deviationStatus = deviationUSD.compareTo(usdRange.negate()) >= 0 && deviationUSD.compareTo(usdRange) <= 0
                                    ? DeviationStatus.YELLOW
                                    : deviationUSD.compareTo(usdRange.negate()) < 0
                                    ? DeviationStatus.RED
                                    : DeviationStatus.GREEN;
                        } else {
                            deviationStatus = DeviationStatus.YELLOW;
                        }
                    } else {
                        deviationStatus = DeviationStatus.NONE;
                    }

                    return BalancesDto.builder()
                            .currencyId(currencyId)
                            .currencyName(currencyName)
                            .usdRate(usdRate)
                            .btcRate(btcRate)
                            .totalWalletBalance(externalTotalBalance)
                            .totalWalletBalanceUSD(externalTotalBalanceUSD)
                            .totalWalletBalanceBTC(externalTotalBalanceBTC)
                            .totalExratesBalance(internalTotalBalance)
                            .totalExratesBalanceUSD(internalTotalBalanceUSD)
                            .totalExratesBalanceBTC(internalTotalBalanceBTC)
                            .deviation(deviation)
                            .deviationUSD(deviationUSD)
                            .deviationBTC(deviationBTC)
                            .signOfMonitoring(signOfMonitoring)
                            .deviationStatus(deviationStatus)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }
}