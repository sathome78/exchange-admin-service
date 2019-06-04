package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.api.BalanceDto;

import java.util.List;

public interface WalletBalancesRepository {

    List<BalanceDto> getAllWalletBalances();

    void updateCurrencyWalletBalances(List<BalanceDto> balances);
}