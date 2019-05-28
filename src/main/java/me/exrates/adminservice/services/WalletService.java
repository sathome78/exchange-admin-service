package me.exrates.adminservice.services;

import me.exrates.adminservice.models.ExternalWalletBalancesDto;
import me.exrates.adminservice.models.InternalWalletBalancesDto;

import java.util.List;

public interface WalletService {

    List<ExternalWalletBalancesDto> getExternalWalletBalances();

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    List<InternalWalletBalancesDto> getWalletBalances();

    void updateExternalMainWalletBalances();

    void updateExternalReservedWalletBalances();

    void updateInternalWalletBalances();
}
