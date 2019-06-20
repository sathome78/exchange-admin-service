package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;

public interface CoreCompanyWalletRepository {

    CoreCompanyWalletDto findByCurrency(CoreCurrencyDto currency);

    boolean update(CoreCompanyWalletDto companyWallet);
}