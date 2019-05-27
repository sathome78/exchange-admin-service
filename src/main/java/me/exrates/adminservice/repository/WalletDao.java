package me.exrates.adminservice.repository;


import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletDao {

    ExternalWalletBalancesDto getExternalMainWalletBalanceByCurrencyName(String currencyName);

    List<ExternalWalletBalancesDto> getExternalMainWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

//    void updateExternalMainWalletBalances(ExternalWalletBalancesDto externalWalletBalancesDto);

//    void updateInternalWalletBalances(InternalWalletBalancesDto internalWalletBalancesDto);

    List<InternalWalletBalancesDto> getWalletBalances();

//    void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate);
}
