package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.repository.AdminTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_JDBC_OPS;
import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
@Log4j2
public class AdminTransactionRepositoryImpl implements AdminTransactionRepository {

    private final JdbcOperations jdbcTemplate;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public AdminTransactionRepositoryImpl(@Qualifier(ADMIN_JDBC_OPS) JdbcOperations jdbcTemplate,
                                          @Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
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
}
