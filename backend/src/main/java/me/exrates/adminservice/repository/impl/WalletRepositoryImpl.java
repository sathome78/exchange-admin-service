package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.repository.WalletRepository;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;

@Log4j2
@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private final NamedParameterJdbcOperations npJdbcTemplate;
    private final JdbcOperations jdbcTemplate;

    @Autowired
    public WalletRepositoryImpl(@Qualifier("adminNPTemplate") NamedParameterJdbcOperations npJdbcTemplate,
                                @Qualifier("adminTemplate") JdbcOperations jdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ExternalWalletBalancesDto> getExternalMainWalletBalances() {
        String sql = "SELECT cewb.currency_id , " +
                "cewb.currency_name, " +
                "cewb.usd_rate, " +
                "cewb.btc_rate, " +
                "cewb.main_balance,  " +
                "cewb.reserved_balance, " +
                "cewb.accounting_imbalance, " +
                "cewb.total_balance, " +
                "cewb.total_balance_usd, " +
                "cewb.total_balance_btc, " +
                "cewb.last_updated_at, " +
                "cewb.sign_of_monitoring, " +
                "cewb.coin_range, " +
                "cewb.check_coin_range, " +
                "cewb.usd_range, " +
                "cewb.check_usd_range" +
                " FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " ORDER BY cewb.currency_id";

        return npJdbcTemplate.query(sql, (rs, row) -> ExternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .mainBalance(rs.getBigDecimal("main_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .accountingImbalance(rs.getBigDecimal("accounting_imbalance"))
                .totalBalance(rs.getBigDecimal("total_balance"))
                .totalBalanceUSD(rs.getBigDecimal("total_balance_usd"))
                .totalBalanceBTC(rs.getBigDecimal("total_balance_btc"))
                .lastUpdatedDate(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .signOfMonitoring(rs.getBoolean("sign_of_monitoring"))
                .coinRange(rs.getBigDecimal("coin_range"))
                .checkCoinRange(rs.getBoolean("check_coin_range"))
                .usdRange(rs.getBigDecimal("usd_range"))
                .checkUsdRange(rs.getBoolean("check_usd_range"))
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
    public void updateExternalMainWalletBalances(List<ExternalWalletBalancesDto> externalWalletBalances) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.usd_rate = ?, cewb.btc_rate = ?, " +
                "cewb.main_balance = ?, " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance + cewb.accounting_imbalance, " +
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
        int updated = npJdbcTemplate.update(sql, params);
        if (updated <= 0) {
            throw new RuntimeException("External reserved wallet balance has not updated");
        }

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance + cewb.accounting_imbalance, " +
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
        updated = npJdbcTemplate.update(sql, params);
        if (updated <= 0) {
            throw new RuntimeException("External common wallet balance has not updated");
        }
    }

    @Override
    public void createReservedWalletAddress(int currencyId) {
        final String sql = "INSERT INTO COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS (currency_id) VALUES (:currency_id)";

        int updated = npJdbcTemplate.update(sql, singletonMap("currency_id", currencyId));
        if (updated <= 0) {
            throw new RuntimeException("External reserved wallet balance has not created");
        }
    }

    @Override
    public void deleteReservedWalletAddress(int id, int currencyId) {
        String sql = "DELETE FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS WHERE id = :id";

        int updated = npJdbcTemplate.update(sql, singletonMap("id", id));
        if (updated <= 0) {
            throw new RuntimeException("External reserved wallet balance has not deleted");
        }

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance + cewb.accounting_imbalance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate" +
                " WHERE cewb.currency_id = :currency_id";

        updated = npJdbcTemplate.update(sql, singletonMap("currency_id", currencyId));
        if (updated <= 0) {
            throw new RuntimeException("External common wallet balance has not updated");
        }
    }

    @Override
    public void updateReservedWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto) {
        String nameSql = StringUtils.EMPTY;
        if (nonNull(externalReservedWalletAddressDto.getName())) {
            nameSql = "cwera.name = :name,";
        }
        String sql = String.format("UPDATE COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " SET cwera.currency_id = :currency_id, %s cwera.wallet_address = :wallet_address, cwera.balance = :balance" +
                " WHERE cwera.id = :id", nameSql);

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("id", externalReservedWalletAddressDto.getId());
                put("currency_id", externalReservedWalletAddressDto.getCurrencyId());
                put("name", externalReservedWalletAddressDto.getName());
                put("wallet_address", externalReservedWalletAddressDto.getWalletAddress());
                put("balance", externalReservedWalletAddressDto.getBalance());
            }
        };
        int updated = npJdbcTemplate.update(sql, params);
        if (updated <= 0) {
            throw new RuntimeException("External reserved wallet balance has not updated");
        }

        sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.reserved_balance = IFNULL((SELECT SUM(cwera.balance) FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera WHERE cwera.currency_id = :currency_id GROUP BY cwera.currency_id), 0), " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance + cewb.accounting_imbalance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE cewb.currency_id = :currency_id";

        updated = npJdbcTemplate.update(sql, Collections.singletonMap("currency_id", externalReservedWalletAddressDto.getCurrencyId()));
        if (updated <= 0) {
            throw new RuntimeException("External common wallet balance has not updated");
        }
    }

    @Override
    public List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId) {
        final String sql = "SELECT cwera.id, cwera.currency_id, cwera.name, cwera.wallet_address, cwera.balance" +
                " FROM COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS cwera" +
                " WHERE cwera.currency_id = :currency_id";

        Map<String, String> params = Collections.singletonMap("currency_id", currencyId);

        return npJdbcTemplate.query(sql, params, (rs, row) -> ExternalReservedWalletAddressDto.builder()
                .id(rs.getInt("id"))
                .currencyId(rs.getInt("currency_id"))
                .name(rs.getString("name"))
                .walletAddress(rs.getString("wallet_address"))
                .balance(rs.getBigDecimal("balance"))
                .build());
    }

    @Override
    public BigDecimal retrieveSummaryUSD() {
        String sql = "SELECT SUM(cewb.total_balance_usd) FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb";

        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal retrieveSummaryBTC() {
        String sql = "SELECT SUM(cewb.total_balance_btc) FROM COMPANY_EXTERNAL_WALLET_BALANCES cewb";

        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public void updateAccountingImbalance(String currencyName, BigDecimal accountingProfit, BigDecimal accountingManualBalanceChanges) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.accounting_imbalance = :accounting_imbalance, " +
                "cewb.total_balance = cewb.main_balance + cewb.reserved_balance + cewb.accounting_imbalance, " +
                "cewb.total_balance_usd = cewb.total_balance * cewb.usd_rate, " +
                "cewb.total_balance_btc = cewb.total_balance * cewb.btc_rate, " +
                "cewb.last_updated_at = CURRENT_TIMESTAMP" +
                " WHERE cewb.currency_name = :currency_name";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("currency_name", currencyName);
                put("accounting_imbalance", accountingProfit.add(accountingManualBalanceChanges));
            }
        };

        int updated = npJdbcTemplate.update(sql, params);
        if (updated <= 0) {
            throw new RuntimeException("Accounting imbalance has not updated");
        }
    }

    @Override
    public void updateSignOfMonitoringForCurrency(int currencyId, boolean signOfMonitoring) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.sign_of_monitoring = :sign_of_monitoring" +
                " WHERE currency_id = :currency_id";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("sign_of_monitoring", signOfMonitoring);
                put("currency_id", currencyId);
            }
        };

        int updated = npJdbcTemplate.update(sql, params);
        if (updated <= 0) {
            throw new RuntimeException("Sign of monitoring has not updated");
        }
    }

    @Override
    public void updateMonitoringRangeForCurrency(int currencyId, BigDecimal coinRange, boolean checkByCoinRange,
                                                 BigDecimal usdRange, boolean checkByUsdRange) {
        final String sql = "UPDATE COMPANY_EXTERNAL_WALLET_BALANCES cewb" +
                " SET cewb.coin_range = :coin_range, cewb.check_coin_range = :check_coin_range," +
                " cewb.usd_range = :usd_range, cewb.check_usd_range = :check_usd_range" +
                " WHERE currency_id = :currency_id";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("coin_range", coinRange);
                put("check_coin_range", checkByCoinRange);
                put("usd_range", usdRange);
                put("check_usd_range", checkByUsdRange);
                put("currency_id", currencyId);
            }
        };

        int updated = npJdbcTemplate.update(sql, params);
        if (updated <= 0) {
            throw new RuntimeException("Monitoring range has not updated");
        }
    }
}