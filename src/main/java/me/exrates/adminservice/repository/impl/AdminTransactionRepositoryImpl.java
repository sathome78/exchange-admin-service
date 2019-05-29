package me.exrates.adminservice.repository.impl;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.repository.AdminTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class AdminTransactionRepositoryImpl implements AdminTransactionRepository {

    private final JdbcOperations jdbcTemplate;

    @Autowired
    public AdminTransactionRepositoryImpl(@Qualifier("template") JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean batchInsert(List<CoreTransaction> transactions) {
        final String sql = "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            }
            @Override
            public int getBatchSize() {
                return transactions.size();
            }
        });
        return rows.length == transactions.size();
    }
}
