package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;

import java.math.BigDecimal;

public interface CoreCompanyWalletService {

    CoreCompanyWalletDto findByCurrency(CoreCurrencyDto currency);

    void deposit(CoreCompanyWalletDto companyWallet, BigDecimal amount, BigDecimal commissionAmount);
}