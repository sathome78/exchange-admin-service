package me.exrates.adminservice.core.repository.impl;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CoreTransactionRepositoryImpl implements CoreTransactionRepository {

    private final NamedParameterJdbcTemplate coreJdbcTemplate;

    @Autowired
    public CoreTransactionRepositoryImpl(@Qualifier("coreTemplate") NamedParameterJdbcTemplate coreJdbcTemplate) {
        this.coreJdbcTemplate = coreJdbcTemplate;
    }

    @Override
    public List<CoreTransaction> findAllLimited(int limit, int position) {
        String sql = "SELECT t.id, w.user_id, C.name as currency_name, t.amount, t.commission_amount, t.source_type, " +
                "UPPER(OT.name) as operation_type, t.datetime," +
                "CASE C.name WHEN 'usd' THEN 1 ELSE NULL END AS rate_in_usd, " +
                "CASE C.name WHEN 'btc' THEN 1 ELSE NULL END AS rate_in_btc " +
                "FROM TRANSACTION t " +
                "LEFT JOIN WALLET w ON w.id = t.user_wallet_id " +
                "LEFT JOIN CURRENCY C on t.currency_id = C.id " +
                "LEFT JOIN OPERATION_TYPE OT on t.operation_type_id = OT.id " +
                "WHERE t.id > :position " +
                "ORDER BY t.id ASC " +
                "LIMIT :size";
        MapSqlParameterSource params = new MapSqlParameterSource("size", String.valueOf(limit))
                .addValue("position", String.valueOf(position));
        return coreJdbcTemplate.query(sql, params, getRowMapper());
    }

    private RowMapper<CoreTransaction> getRowMapper() {
        return (rs, i) -> CoreTransaction.builder()
                .id(rs.getInt(COL_ID))
                .userId(rs.getInt(COL_USER_ID))
                .currencyName(rs.getString(COL_CURRENCY_NAME))
                .amount(rs.getBigDecimal(COL_AMOUNT))
                .commissionAmount(rs.getBigDecimal(COL_COMMISSION_AMOUNT))
                .sourceType(COL_SOURCE_TYPE)
                .operationType(rs.getString(COL_OPERATION_TYPE))
                .dateTime(rs.getTimestamp(COL_DATETIME).toLocalDateTime())
                .rateInUsd(rs.getBigDecimal(COL_RATE_IN_USD))
                .rateInBtc(rs.getBigDecimal(COL_RATE_IN_BTC))
                .build();
    }
}
