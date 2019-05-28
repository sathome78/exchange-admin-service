package me.exrates.adminservice.daos.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.daos.WalletDao;
import me.exrates.adminservice.models.ExternalWalletBalancesDto;
import me.exrates.adminservice.models.InternalWalletBalancesDto;
import me.exrates.adminservice.models.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Log4j2
@Repository
public class WalletDaoImpl implements WalletDao {

    private final NamedParameterJdbcOperations npJdbcTemplate;
    private final JdbcOperations jdbcTemplate;
    private final NamedParameterJdbcOperations coreNPJdbcTemplate;

    @Autowired
    public WalletDaoImpl(@Qualifier("NPTemplate") NamedParameterJdbcOperations npJdbcTemplate,
                         @Qualifier("template") JdbcOperations jdbcTemplate,
                         @Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreNPJdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.coreNPJdbcTemplate = coreNPJdbcTemplate;
    }

    @Override
    public List<ExternalWalletBalancesDto> getExternalMainWalletBalances() {
        String sql = "SELECT cewb.currency_id , " +
                "cewb.currency_name, " +
                "cewb.usd_rate, " +
                "cewb.btc_rate, " +
                "cewb.main_balance,  " +
                "cewb.reserved_balance, " +
                "cewb.total_balance, " +
                "cewb.total_balance_usd, " +
                "cewb.total_balance_btc, " +
                "cewb.last_updated_at " +
                " FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " ORDER BY cewb.currency_id";

        return npJdbcTemplate.query(sql, (rs, row) -> ExternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .mainBalance(rs.getBigDecimal("main_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        final String sql = "SELECT iwb.currency_id, " +
                "iwb.currency_name, " +
                "iwb.role_id, " +
                "iwb.role_name, " +
                "iwb.usd_rate, " +
                "iwb.btc_rate, " +
                "iwb.total_balance, " +
                "iwb.total_balance_usd, " +
                "iwb.total_balance_btc, " +
                "iwb.last_updated_at" +
                " FROM INTERNAL_WALLET_BALANCES iwb" +
                " ORDER BY iwb.currency_id, iwb.role_id";

        return npJdbcTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        final String sql = "SELECT cur.id AS currency_id, " +
                "cur.name AS currency_name, " +
                "ur.id AS role_id, " +
                "ur.name AS role_name, " +
                "w.active_balance, " +
                "w.reserved_balance" +
                " FROM WALLET w" +
                " JOIN CURRENCY cur ON cur.id = w.currency_id AND cur.hidden = 0" +
                " JOIN USER u ON u.id = w.user_id" +
                " JOIN USER_ROLE ur ON ur.id = u.roleid" +
                " GROUP BY cur.id, ur.id" +
                " ORDER BY cur.id, ur.id";

        return coreNPJdbcTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .totalBalance(rs.getBigDecimal("active_balance").add(rs.getBigDecimal("reserved_balance")))
                .build());
    }

    @Override
    public void updateExternalMainWalletBalances(List<ExternalWalletBalancesDto> externalWalletBalances) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.usd_rate = ?, cewb.btc_rate = ?, " +
                "cewb.main_balance = ?, " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = IFNULL(?, cewb.last_updated_at)" +
                " WHERE cewb.currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ExternalWalletBalancesDto exWallet = externalWalletBalances.get(i);
                ps.setBigDecimal(1, exWallet.getUsdRate());
                ps.setBigDecimal(2, exWallet.getBtcRate());
                ps.setBigDecimal(3, exWallet.getMainBalance());
                ps.setTimestamp(4, nonNull(exWallet.getLastUpdatedDate()) ? Timestamp.valueOf(exWallet.getLastUpdatedDate()) : null);
                ps.setString(5, exWallet.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return externalWalletBalances.size();
            }
        });
    }

    @Override
    public void updateInternalWalletBalances(List<InternalWalletBalancesDto> internalWalletBalances) {
        final String sql = "UPDATE INTERNAL_WALLET_BALANCES iwb" +
                " SET iwb.usd_rate = ?, iwb.btc_rate = ?, " +
                "iwb.total_balance = IFNULL(?, 0), " +
                "iwb.total_balance_usd = iwb.total_balance * iwb.usd_rate, " +
                "iwb.total_balance_btc = iwb.total_balance * iwb.btc_rate, " +
                "iwb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE iwb.currency_id = ? AND iwb.role_id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                InternalWalletBalancesDto inWallet = internalWalletBalances.get(i);
                ps.setBigDecimal(1, inWallet.getUsdRate());
                ps.setBigDecimal(2, inWallet.getBtcRate());
                ps.setBigDecimal(3, inWallet.getTotalBalance());
                ps.setInt(4, inWallet.getCurrencyId());
                ps.setInt(5, inWallet.getRoleId());
            }

            @Override
            public int getBatchSize() {
                return internalWalletBalances.size();
            }
        });
    }

    @Override
    public void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate) {
        String sql = "UPDATE COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " SET cwera.balance = :balance" +
                " WHERE cwera.currency_id = :currency_id" +
                " AND cwera.wallet_address = :wallet_address";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_id", currencyId);
                put("wallet_address", walletAddress);
                put("balance", balance);
            }
        };
        npJdbcTemplate.update(sql, params);

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = IFNULL(:last_updated_at, cewb.last_updated_at)" +
                " WHERE cewb.currency_id = :currency_id";

        params = new HashMap<String, Object>() {
            {
                put("currency_id", currencyId);
                put("last_updated_at", lastReservedBalanceUpdate);
            }
        };
        npJdbcTemplate.update(sql, params);
    }
}