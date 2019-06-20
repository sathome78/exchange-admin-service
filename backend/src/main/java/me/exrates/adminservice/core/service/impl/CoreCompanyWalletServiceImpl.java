package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.exceptions.WalletPersistException;
import me.exrates.adminservice.core.repository.CoreCompanyWalletRepository;
import me.exrates.adminservice.core.service.CoreCompanyWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Log4j2
@Service
@Transactional
public class CoreCompanyWalletServiceImpl implements CoreCompanyWalletService {

    private final CoreCompanyWalletRepository coreCompanyWalletRepository;

    @Autowired
    public CoreCompanyWalletServiceImpl(CoreCompanyWalletRepository coreCompanyWalletRepository) {
        this.coreCompanyWalletRepository = coreCompanyWalletRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCompanyWalletDto findByCurrency(CoreCurrencyDto currency) {
        return coreCompanyWalletRepository.findByCurrency(currency);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public void deposit(CoreCompanyWalletDto companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().add(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);

        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!coreCompanyWalletRepository.update(companyWallet)) {
            throw new WalletPersistException("Failed deposit on company wallet " + companyWallet.toString());
        }
    }
}