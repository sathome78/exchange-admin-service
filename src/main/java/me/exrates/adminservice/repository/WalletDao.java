package me.exrates.adminservice.repository;


import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletDao {

    List<ExternalWalletBalancesDto> getExternalMainWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    List<InternalWalletBalancesDto> getWalletBalances();

    void updateExternalMainWalletBalances(List<ExternalWalletBalancesDto> externalWalletBalances);

    void updateInternalWalletBalances(List<InternalWalletBalancesDto> internalWalletBalances);

    void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate);
}
