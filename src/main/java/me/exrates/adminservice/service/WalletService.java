package me.exrates.adminservice.service;

import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.util.List;

public interface WalletService {

    List<ExternalWalletBalancesDto> getExternalWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateExternalMainWalletBalances();

    void updateExternalReservedWalletBalances();

    void updateInternalWalletBalances();

    List<InternalWalletBalancesDto> getWalletBalances();
}
