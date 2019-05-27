package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.api.BalanceDto;

import java.util.List;

public interface WalletBalancesDao {

    BalanceDto getBalancesByCurrencyName(String currencyName);

    List<BalanceDto> getAllWalletBalances();

    boolean addCurrencyWalletBalances(BalanceDto balanceDto);

    boolean updateCurrencyWalletBalances(BalanceDto balanceDto);
}
