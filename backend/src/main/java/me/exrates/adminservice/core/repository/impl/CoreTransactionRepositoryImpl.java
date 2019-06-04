package me.exrates.adminservice.core.repository.impl;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Repository
public class CoreTransactionRepositoryImpl implements CoreTransactionRepository {

    private final NamedParameterJdbcOperations coreJdbcTemplate;

    @Autowired
    public CoreTransactionRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreJdbOps) {
        this.coreJdbcTemplate = coreJdbOps;
    }

    @Override
    public List<CoreTransaction> findAllLimited(int limit, long position) {
        String sql = "SELECT t.id, w.user_id, C.name as currency_name, t.amount, t.commission_amount, t.source_type, " +
                "UPPER(OT.name) as operation_type, t.datetime," +
                "CASE C.name WHEN 'USD' THEN 1 ELSE NULL END AS rate_in_usd, " +
                "CASE C.name WHEN 'BTC' THEN 1 ELSE NULL END AS rate_in_btc " +
                "FROM TRANSACTION t " +
                "LEFT JOIN WALLET w ON w.id = t.user_wallet_id " +
                "LEFT JOIN CURRENCY C on t.currency_id = C.id " +
                "LEFT JOIN OPERATION_TYPE OT on t.operation_type_id = OT.id " +
                "WHERE t.id > :position " +
                "ORDER BY t.id ASC " +
                "LIMIT :size";
        MapSqlParameterSource params = new MapSqlParameterSource("size", limit)
                .addValue("position", position);
        return coreJdbcTemplate.query(sql, params, getRowMapper());
    }

    private RowMapper<CoreTransaction> getRowMapper() {
        return (rs, i) -> CoreTransaction.builder()
                .id(rs.getInt(COL_ID))
                .userId(rs.getInt(COL_USER_ID))
                .currencyName(rs.getString(COL_CURRENCY_NAME))
                .amount(getBigDecimal(rs.getBigDecimal(COL_AMOUNT)))
                .commissionAmount(getBigDecimal(rs.getBigDecimal(COL_COMMISSION_AMOUNT)))
                .sourceType(rs.getString(COL_SOURCE_TYPE))
                .operationType(rs.getString(COL_OPERATION_TYPE))
                .dateTime(rs.getTimestamp(COL_DATETIME).toLocalDateTime())
                .rateInUsd(getBigDecimal(rs.getBigDecimal(COL_RATE_IN_USD), 2))
                .rateInBtc(getBigDecimal(rs.getBigDecimal(COL_RATE_IN_BTC), 8))
                .build();
    }

    private BigDecimal getBigDecimal(BigDecimal value, int scale) {
        return Objects.isNull(value)
                ? BigDecimal.ZERO
                : value.setScale(scale, RoundingMode.HALF_DOWN).stripTrailingZeros();
    }

    private BigDecimal getBigDecimal(BigDecimal value) {
        return getBigDecimal(value, 8);
    }
}
