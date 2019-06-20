package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.core.service.CoreWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class CoreWalletServiceImpl implements CoreWalletService {

    private final CoreWalletRepository coreWalletRepository;

    @Autowired
    public CoreWalletServiceImpl(CoreWalletRepository coreWalletRepository) {
        this.coreWalletRepository = coreWalletRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreWalletDto findByUserAndCurrency(Integer userId, Integer currencyId) {
        return coreWalletRepository.findByUserAndCurrency(userId, currencyId);
    }

    @Override
    public boolean isUserAllowedToManuallyChangeWalletBalance(Integer adminId, Integer userId) {
        return coreWalletRepository.isUserAllowedToManuallyChangeWalletBalance(adminId, userId);
    }

    @Override
    public WalletTransferStatus walletBalanceChange(CoreWalletOperationDto walletOperation) {
        return coreWalletRepository.walletBalanceChange(walletOperation);
    }
}
