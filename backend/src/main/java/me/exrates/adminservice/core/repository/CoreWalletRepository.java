package me.exrates.adminservice.core.repository;


import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.util.List;

public interface CoreWalletRepository {

    List<InternalWalletBalancesDto> getWalletBalances();
}