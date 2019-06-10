package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.api.BalanceDto;

import java.util.List;

public interface WalletBalancesRepository {

    String TABLE_NAME = "CURRENT_CURRENCY_BALANCES";

    List<BalanceDto> getAllWalletBalances();

    void updateCurrencyWalletBalances(List<BalanceDto> balances);
}