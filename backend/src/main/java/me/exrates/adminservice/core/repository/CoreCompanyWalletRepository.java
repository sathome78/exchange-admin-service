package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;

public interface CoreCompanyWalletRepository {

    String TABLE_NAME = "COMPANY_WALLET";

    CoreCompanyWalletDto findByCurrency(CoreCurrencyDto currency);

    boolean update(CoreCompanyWalletDto companyWallet);
}