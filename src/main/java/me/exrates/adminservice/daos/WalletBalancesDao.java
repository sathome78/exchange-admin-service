package me.exrates.adminservice.daos;

import me.exrates.adminservice.models.api.BalanceDto;

import java.util.List;

public interface WalletBalancesDao {

    BalanceDto getBalancesByCurrencyName(String currencyName);

    List<BalanceDto> getAllWalletBalances();

    boolean addCurrencyWalletBalances(BalanceDto balanceDto);

    boolean updateCurrencyWalletBalances(BalanceDto balanceDto);
}
