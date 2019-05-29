package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
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
}