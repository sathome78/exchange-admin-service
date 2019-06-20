package me.exrates.adminservice.core.repository;


import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;

import java.util.List;

public interface CoreWalletRepository {

    List<InternalWalletBalancesDto> getWalletBalances();

    CoreWalletDto findByUserAndCurrency(int userId, int currencyId);

    boolean isUserAllowedToManuallyChangeWalletBalance(Integer adminId, Integer userId);

    WalletTransferStatus walletBalanceChange(CoreWalletOperationDto walletOperation);
}