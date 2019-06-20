package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.domain.CoreTransactionDto;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
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
                "UPPER(OT.name) as operation_type, t.datetime, t.source_id as source_id, " +
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

    @Override
    public CoreTransactionDto create(CoreTransactionDto transaction) {
        final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
                " commission_id, operation_type_id, currency_id, merchant_id, datetime, order_id, confirmation, provided," +
                " active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before, " +
                " source_type, source_id, description)" +
                " VALUES (:user_wallet, :company_wallet, :amount, :commission_amount, :commission, :operation_type, :currency, " +
                ":merchant, :datetime, :order_id, :confirmation, :provided, :active_balance_before, :reserved_balance_before, " +
                ":company_balance_before, :company_commission_balance_before, :source_type, :source_id, :description)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("user_wallet", transaction.getUserWallet().getId());
                    put("company_wallet", transaction.getCompanyWallet() == null ? null : transaction.getCompanyWallet().getId());
                    put("amount", transaction.getAmount());
                    put("commission_amount", transaction.getCommissionAmount());
                    put("commission", transaction.getCommission() == null ? null : transaction.getCommission().getId());
                    put("operation_type", transaction.getOperationType().type);
                    put("currency", transaction.getCurrency().getId());
                    put("merchant", transaction.getMerchant() == null ? null : transaction.getMerchant().getId());
                    put("datetime", transaction.getDatetime() == null ? null : Timestamp.valueOf(transaction.getDatetime()));
                    put("order_id", transaction.getOrder() == null ? null : transaction.getOrder().getId());
                    put("confirmation", transaction.getConfirmation());
                    put("provided", transaction.isProvided());
                    put("active_balance_before", transaction.getActiveBalanceBefore());
                    put("reserved_balance_before", transaction.getReservedBalanceBefore());
                    put("company_balance_before", transaction.getCompanyBalanceBefore());
                    put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                    put("source_type", transaction.getSourceType() == null ? null : transaction.getSourceType().toString());
                    put("source_id", transaction.getSourceId());
                    put("description", transaction.getDescription());
                }
            };
            if (coreJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder) > 0) {
                transaction.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
                return transaction;
            }
        } catch (Exception ex) {
            log.error("exception: ", ex);
        }
        throw new RuntimeException("Transaction creating failed");
    }

    @Override
    public boolean updateForProvided(CoreTransactionDto transaction) {
        final String sql = "UPDATE TRANSACTION tr" +
                " SET tr.provided = :provided, " +
                "     tr.active_balance_before = :active_balance_before, " +
                "     tr.reserved_balance_before = :reserved_balance_before, " +
                "     tr.company_balance_before = :company_balance_before, " +
                "     tr.company_commission_balance_before = :company_commission_balance_before, " +
                "     tr.source_type = :source_type, " +
                "     tr.source_id = :source_id, " +
                "     tr.provided_modification_date = NOW() " +
                " WHERE tr.id = :id";
        final int PROVIDED = 1;
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("provided", PROVIDED);
                put("id", transaction.getId());
                put("active_balance_before", transaction.getActiveBalanceBefore());
                put("reserved_balance_before", transaction.getReservedBalanceBefore());
                put("company_balance_before", transaction.getCompanyBalanceBefore());
                put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                put("source_type", transaction.getSourceType().name());
                put("source_id", transaction.getSourceId());
            }
        };
        return coreJdbcTemplate.update(sql, params) > 0;
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
                .sourceId(rs.getInt(COL_SOURCE_ID))
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
