package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.api.BalanceDto;

import java.util.List;

public interface WalletBalancesDao {

    List<BalanceDto> getAllWalletBalances();

    void updateCurrencyWalletBalances(List<BalanceDto> balances);
}
