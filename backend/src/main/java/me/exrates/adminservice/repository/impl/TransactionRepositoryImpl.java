package me.exrates.adminservice.repository.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        final String sql = "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final int[] rows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CoreTransaction transaction = transactions.get(i);
                ps.setInt(1, transaction.getId());
                ps.setInt(2, transaction.getUserId());
                ps.setString(3, transaction.getCurrencyName());
                ps.setBigDecimal(4, transaction.getBalanceBefore());
                ps.setBigDecimal(5, transaction.getAmount());
                ps.setBigDecimal(6, transaction.getCommissionAmount());
                ps.setString(7, transaction.getSourceType());
                ps.setString(8, transaction.getOperationType());
                ps.setTimestamp(9, Timestamp.valueOf(transaction.getDateTime()));
                ps.setBigDecimal(10, transaction.getRateInUsd());
                ps.setBigDecimal(11, transaction.getRateInBtc());
                ps.setBigDecimal(12, transaction.getRateBtcForOneUsd());
                ps.setInt(13, transaction.getSourceId());
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

    @Override
    public Map<Integer, List<Integer>> findUsersRefills(Collection<Integer> usersIds) {
        String sql = "SELECT " + COL_USER_ID + " FROM " + TABLE +
                " WHERE " + COL_SOURCE_TYPE + " = \'REFILL\' AND " + COL_OPERATION_TYPE + " = \'INPUT\'" +
                " AND " + COL_USER_ID + " IN (:ids)";
        Map<String, Object> params = Collections.singletonMap("ids", usersIds);
        return namedParameterJdbcOperations.query(sql, params, rs -> {
            Map<Integer, List<Integer>> refills = Maps.newHashMap();
            while (rs.next()) {
                final int userId = rs.getInt(1);
                refills.computeIfPresent(userId, (integer, integers) -> {
                    integers.add(integer);
                    return integers;
                });
                refills.putIfAbsent(userId, Lists.newArrayList(userId));
            }
            return refills;
        });
    }

    @Override
    public Map<Integer, List<CoreTransaction>> findAllTransactions(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Maps.newHashMap();
        }
        String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_USER_ID + " IN (:ids)";
        Map<String, Object> params = Collections.singletonMap("ids", userIds);
        final List<CoreTransaction> transactions = namedParameterJdbcOperations.query(sql, params, getTransactionRowMapper());
        Map<Integer, List<CoreTransaction>> events = Maps.newHashMap();
        transactions.forEach(tr -> {
            events.computeIfPresent(tr.getUserId(), (k, list) -> {
                list.add(tr);
                return list;
            });
            events.putIfAbsent(tr.getUserId(), Lists.newArrayList(tr));
        });
        return events;
    }

    @Override
    public Set<Integer> findUserIdsWithAnyRefill() {
        String sql = "SELECT user_id FROM TRANSACTIONS WHERE source_type = \'REFIILL\' GROUP BY user_id";
        return namedParameterJdbcOperations.query(sql, Collections.emptyMap(), rs -> {
            Set<Integer> userIds = Sets.newHashSet();
            while (rs.next()) {
                userIds.add(rs.getInt(1));
            }
            return userIds;
        });
    }

    private RowMapper<CoreTransaction> getTransactionRowMapper() {
        return (rs, rowNum) -> CoreTransaction.builder()
                .id(rs.getInt(COL_ID))
                .userId(rs.getInt(COL_USER_ID))
                .currencyName(rs.getString(COL_CURRENCY_NAME))
                .balanceBefore(rs.getBigDecimal(COL_ACTIVE_BALANCE_BEFORE))
                .amount(rs.getBigDecimal(COL_AMOUNT))
                .commissionAmount(rs.getBigDecimal(COL_COMMISSION_AMOUNT))
                .sourceType(rs.getString(COL_SOURCE_TYPE))
                .operationType(rs.getString(COL_OPERATION_TYPE))
                .dateTime(rs.getTimestamp(COL_DATETIME).toLocalDateTime())
                .rateInUsd(rs.getBigDecimal(COL_RATE_IN_USD))
                .rateInBtc(rs.getBigDecimal(COL_RATE_IN_BTC))
                .sourceId(rs.getInt(COL_SOURCE_ID))
                .rateBtcForOneUsd(rs.getBigDecimal(COL_RATE_BTC_FOR_ONE_USD))
                .build();
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
