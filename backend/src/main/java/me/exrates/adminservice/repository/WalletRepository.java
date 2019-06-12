package me.exrates.adminservice.repository;


import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletRepository {

    String TABLE_NAME_1 = "COMPANY_EXTERNAL_WALLET_BALANCES";
    String TABLE_NAME_2 = "COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS";
    String TABLE_NAME_3 = "INTERNAL_WALLET_BALANCES";

    List<ExternalWalletBalancesDto> getExternalMainWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateExternalMainWalletBalances(List<ExternalWalletBalancesDto> externalWalletBalances);

    void updateInternalWalletBalances(List<InternalWalletBalancesDto> internalWalletBalances);

    void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate);

    void createReservedWalletAddress(int currencyId);

    void deleteReservedWalletAddress(int id, int currencyId);

    void updateReservedWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto);

    List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId);

    BigDecimal retrieveSummaryUSD();

    BigDecimal retrieveSummaryBTC();

    void updateAccountingImbalance(String currencyName, BigDecimal accountingProfit, BigDecimal accountingManualBalanceChanges);

    void updateSignOfCertaintyForCurrency(int currencyId, boolean signOfCertainty);
    
    void updateSignOfMonitoringForCurrency(int currencyId, boolean signOfMonitoring);

    void updateMonitoringRangeForCurrency(int currencyId, BigDecimal coinRange, boolean checkByCoinRange, BigDecimal usdRange, boolean checkByUsdRange);
}