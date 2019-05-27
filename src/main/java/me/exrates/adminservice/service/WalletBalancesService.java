package me.exrates.adminservice.service;

import me.exrates.adminservice.domain.api.BalanceDto;

import java.util.List;
import java.util.Map;

public interface WalletBalancesService {

    List<BalanceDto> getAllWalletBalances();

    Map<String, BalanceDto> getCachedBalances();

    void updateCurrencyBalances();
}
