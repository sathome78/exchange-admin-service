package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.BalancesDto;
import me.exrates.adminservice.domain.DashboardOneDto;
import me.exrates.adminservice.domain.DashboardTwoDto;
import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.FilterDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    List<ExternalWalletBalancesDto> getExternalWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    List<InternalWalletBalancesDto> getWalletBalances();

    void updateExternalMainWalletBalances();

    void updateExternalReservedWalletBalances();

    void updateInternalWalletBalances();

    void createWalletAddress(int currencyId);

    void deleteWalletAddress(int id, int currencyId, String walletAddress);

    void updateWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto, boolean isSavedAsAddress);

    List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId);

    BigDecimal getExternalReservedWalletBalance(Integer currencyId, String walletAddress);

    BigDecimal retrieveSummaryUSD();

    BigDecimal retrieveSummaryBTC();

    void updateAccountingImbalance(String currencyName, BigDecimal accountingProfit, BigDecimal accountingManualBalanceChanges);

    boolean updateSignOfMonitoringForCurrency(int currencyId, boolean signOfMonitoring);

    boolean updateMonitoringRangeForCurrency(int currencyId, BigDecimal coinRange, boolean checkByCoinRange, BigDecimal usdRange, boolean checkByUsdRange);

    List<BalancesDto> getBalancesSliceStatistic(FilterDto filter);

    DashboardOneDto getDashboardOne();

    DashboardTwoDto getDashboardTwo(FilterDto filter);
}