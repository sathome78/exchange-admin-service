package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCompanyWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class CoreCompanyWalletRepositoryImpl implements CoreCompanyWalletRepository {

    private final NamedParameterJdbcOperations coreTemplate;

    @Autowired
    public CoreCompanyWalletRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreTemplate) {
        this.coreTemplate = coreTemplate;
    }

    @Override
    public CoreCompanyWalletDto findByCurrency(CoreCurrencyDto currency) {
        final String sql = "SELECT * FROM COMPANY_WALLET cw WHERE cw.currency_id = :currency_id";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", currency.getId());
            }
        };

        try {
            return coreTemplate.queryForObject(sql, params, (resultSet, i) -> {
                CoreCompanyWalletDto companyWallet = new CoreCompanyWalletDto();
                companyWallet.setId(resultSet.getInt("id"));
                companyWallet.setBalance(resultSet.getBigDecimal("balance"));
                companyWallet.setCommissionBalance(resultSet.getBigDecimal("commission_balance"));
                companyWallet.setCurrency(currency);
                return companyWallet;
            });
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean update(CoreCompanyWalletDto companyWallet) {
        final String sql = "UPDATE COMPANY_WALLET cw " +
                "SET cw.balance = :balance, cw.commission_balance = :commission_balance " +
                "WHERE cw.id = :id";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("balance", companyWallet.getBalance());
                put("commission_balance", companyWallet.getCommissionBalance());
                put("id", companyWallet.getId());
            }
        };
        return coreTemplate.update(sql, params) > 0;
    }
}