package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.repository.WalletBalancesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Log4j2
@Repository
public class WalletBalancesDaoImpl implements WalletBalancesDao {

    private final NamedParameterJdbcTemplate npJdbcTemplate;
    private final JdbcOperations jdbcTemplate;

    @Autowired
    public WalletBalancesDaoImpl(@Qualifier("adminTemplate") NamedParameterJdbcTemplate npJdbcTemplate,
                                 @Qualifier("template") JdbcOperations jdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<BalanceDto> getAllWalletBalances() {
        final String sql = "SELECT ccb.currency_name, ccb.balance, ccb.last_updated_at FROM CURRENT_CURRENCY_BALANCES ccb";

        return npJdbcTemplate.query(sql, (rs, row) -> BalanceDto.builder()
                .currencyName(rs.getString("currency_name"))
                .balance(rs.getBigDecimal("balance"))
                .lastUpdatedAt(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Override
    public void updateCurrencyWalletBalances(List<BalanceDto> balances) {
        final String sql = "UPDATE CURRENT_CURRENCY_BALANCES ccb " +
                "SET ccb.balance = ?, ccb.last_updated_at = ? " +
                "WHERE ccb.currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                BalanceDto balanceDto = balances.get(i);
                ps.setBigDecimal(1, balanceDto.getBalance());
                ps.setTimestamp(2, Timestamp.valueOf(balanceDto.getLastUpdatedAt()));
                ps.setString(3, balanceDto.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return balances.size();
            }
        });
    }
}
