package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.api.BalanceDto;

public interface WalletBalancesDao {

    BalanceDto getBalancesByCurrencyName(String currencyName);

    boolean addCurrencyWalletBalances(BalanceDto balanceDto);

    boolean updateCurrencyWalletBalances(BalanceDto balanceDto);
}
