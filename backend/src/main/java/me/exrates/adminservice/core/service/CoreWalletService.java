package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;

public interface CoreWalletService {

    CoreWalletDto findByUserAndCurrency(Integer userId, Integer currencyId);

    boolean isUserAllowedToManuallyChangeWalletBalance(Integer adminEmail, Integer userId);

    WalletTransferStatus walletBalanceChange(CoreWalletOperationDto walletOperation);
}
