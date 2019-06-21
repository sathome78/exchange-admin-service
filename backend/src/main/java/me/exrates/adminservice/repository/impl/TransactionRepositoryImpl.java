package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.repository.TransactionRepository;
import me.exrates.adminservice.utils.CurrencyTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_JDBC_OPS;
import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
@Log4j2
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JdbcOperations jdbcTemplate;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final CoreUserRepository coreUserRepository;

    @Autowired
    public TransactionRepositoryImpl(@Qualifier(ADMIN_JDBC_OPS) JdbcOperations jdbcTemplate,
                                     @Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations namedParameterJdbcOperations, CoreUserRepository coreUserRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.coreUserRepository = coreUserRepository;
    }

    @Override
    public boolean batchInsert(List<CoreTransaction> transactions) {
        final String sql = "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final int[] rows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CoreTransaction transaction = transactions.get(i);
                ps.setInt(1, transaction.getId());
                ps.setInt(2, transaction.getUserId());
                ps.setString(3, transaction.getCurrencyName());
                ps.setBigDecimal(4, transaction.getAmount());
                ps.setBigDecimal(5, transaction.getCommissionAmount());
                ps.setString(6, transaction.getSourceType());
                ps.setString(7, transaction.getOperationType());
                ps.setTimestamp(8, Timestamp.valueOf(transaction.getDateTime()));
                ps.setBigDecimal(9, transaction.getRateInUsd());
                ps.setBigDecimal(10, transaction.getRateInBtc());
                ps.setBigDecimal(11, transaction.getRateBtcForOneUsd());
                ps.setInt(12, transaction.getSourceId());
            }

            @Override
            public int getBatchSize() {
                return transactions.size();
            }
        });
        return rows.length == transactions.size();
    }

    @Override
    public Optional<Long> findMaxId() {
        try {
            String sql = "SELECT MAX(" + COL_ID + ") FROM " + TABLE;
            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject(sql, Collections.emptyMap(), Long.class));
        } catch (DataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to find max id in " + TABLE, e);
            }
            return Optional.of(-1L);
        }
    }

    @Override
    public Collection<CurrencyTuple> getDailyTradeCommission() {
        String sql = "SELECT " + COL_CURRENCY_NAME + ", (" + COL_COMMISSION_AMOUNT + " * " + COL_RATE_IN_USD + ") AS usd_com," +
                " (" + COL_COMMISSION_AMOUNT + " * " + COL_RATE_IN_BTC + ") AS btc_com, " + COL_RATE_BTC_FOR_ONE_USD +
                " FROM " + TABLE +
                " WHERE " + COL_DATETIME + " >= CURRENT_TIMESTAMP - INTERVAL 24 HOUR AND " + COL_SOURCE_TYPE + " = \'ORDER\'" +
                " AND " + COL_OPERATION_TYPE + " IN ('INPUT', 'OUTPUT')";
        return namedParameterJdbcOperations.query(sql, Collections.emptyMap(), getCurrencyTupleRowMapper());
    }

    @Override
    public Map<String, BigDecimal> getDailyInnerTradeVolume() {
        String botCondition = "";
        Map<String, Object> params = new HashMap<>();
        final Collection<Integer> botsIds = coreUserRepository.getBotsIds();
        if (!botsIds.isEmpty()) {
            botCondition = " AND " + COL_USER_ID + " NOT IN (:ids)";
            params.put("ids", botsIds);
        }
        String sql = "SELECT SUM(" + COL_AMOUNT + " * " + COL_RATE_IN_USD + ") AS usd_volume," +
                " SUM(" + COL_AMOUNT + " * " + COL_RATE_IN_BTC + ") AS btc_volume " +
                " FROM " + TABLE +
                " WHERE " + COL_DATETIME + " >= CURRENT_TIMESTAMP - INTERVAL 24 HOUR AND " + COL_SOURCE_TYPE + " = \'ORDER\'" +
                " AND " + COL_OPERATION_TYPE + " IN ('INPUT', 'OUTPUT')" + botCondition;
        return namedParameterJdbcOperations.query(sql, params, rs -> {
            rs.next();
            Map<String, BigDecimal> values1 = new HashMap<>();
            values1.put("USD", rs.getBigDecimal("usd_volume"));
            values1.put("BTC", rs.getBigDecimal("btc_volume"));
            return values1;
        });
    }

    private RowMapper<CurrencyTuple> getCurrencyTupleRowMapper() {
        return (rs, i) -> CurrencyTuple.builder()
                .currencyName(rs.getString(COL_CURRENCY_NAME))
                .btcAmount(rs.getBigDecimal("btc_com"))
                .usdAmount(rs.getBigDecimal("usd_com"))
                .rateBtcForOneUsd(rs.getBigDecimal(COL_RATE_BTC_FOR_ONE_USD))
                .build();
    }

}
