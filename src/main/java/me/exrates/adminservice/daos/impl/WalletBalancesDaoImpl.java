package me.exrates.adminservice.daos.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.daos.WalletBalancesDao;
import me.exrates.adminservice.models.api.BalanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class WalletBalancesDaoImpl implements WalletBalancesDao {

    private final NamedParameterJdbcOperations npJdbcTemplate;

    @Autowired
    public WalletBalancesDaoImpl(@Qualifier("NPTemplate") NamedParameterJdbcOperations npJdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
    }

    @Override
    public BalanceDto getBalancesByCurrencyName(String currencyName) {
        final String sql = "SELECT * FROM CURRENT_CURRENCY_BALANCES ccb WHERE ccb.currency_name = :currency_name";

        try {
            return npJdbcTemplate.queryForObject(sql, Collections.singletonMap("currency_name", currencyName), BalanceDto.class);
        } catch (Exception ex) {
            log.debug("Currency with name: {} not found", currencyName);
            return null;
        }
    }

    @Override
    public boolean addCurrencyWalletBalances(BalanceDto balanceDto) {
        final String sql = "INSERT INTO CURRENT_CURRENCY_BALANCES (currency_name, balance, last_updated_at) VALUES (:currency_name, :balance, :last_updated_at)";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_name", balanceDto.getCurrencyName());
        params.put("balance", balanceDto.getBalance());
        params.put("last_updated_at", Timestamp.valueOf(balanceDto.getLastUpdatedAt()));

        return npJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateCurrencyWalletBalances(BalanceDto balanceDto) {
        final String sql = "UPDATE CURRENT_CURRENCY_BALANCES ccb SET ccb.balance = :balance, ccb.last_updated_at = :last_updated_at";

        Map<String, Object> params = new HashMap<>();
        params.put("balance", balanceDto.getBalance());
        params.put("last_updated_at", Timestamp.valueOf(balanceDto.getLastUpdatedAt()));

        return npJdbcTemplate.update(sql, params) > 0;
    }
}