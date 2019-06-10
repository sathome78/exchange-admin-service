package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.repository.AdminUserInsightsRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
@Log4j2
public class AdminUserInsightsRepositoryImpl implements AdminUserInsightsRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public AdminUserInsightsRepositoryImpl(@Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public PagedResult<UserInsight> findAll(int limit, int offset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String limitCondition = " LIMIT 20 ";
        if (limit > 0) {
            limitCondition = "LIMIT :size";
            params.addValue("size", limit);
        }
        String offsetCondition = "";
        if (offset > 0) {
            offsetCondition = " OFFSET :step ";
            params.addValue("step", offset);
        }
        String sql = "SELECT * FROM " + TABLE + " ORDER BY " + COL_CREATED + " ASC, " + COL_USER_ID + " ASC "
                + limitCondition + offsetCondition;
        final List<UserInsight> insights = namedParameterJdbcOperations.query(sql, params, getRowMapper());
        return new PagedResult<>(getTotalAmount(), insights);
    }

    private int getTotalAmount() {
        try {
            String sqlCount = "SELECT COUNT(*) FROM " + TABLE;
            Optional<Integer> result = Optional.ofNullable(namedParameterJdbcOperations.queryForObject(sqlCount, Collections.emptyMap(), Integer.class));
            return result.orElse(0);
        } catch (DataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("No records found for table " + TABLE);
            }
            return 0;
        }
    }

    private RowMapper<UserInsight> getRowMapper() {
        return (rs, i) -> UserInsight.builder()
                .created(rs.getDate(COL_CREATED).toLocalDate())
                .userId(rs.getInt(COL_USER_ID))
                .rateBtcForOneUsd(rs.getBigDecimal(COL_RATE_BTC_FOR_ONE_USD))
                .refillAmountUsd(rs.getBigDecimal(COL_REFILL_AMOUNT_USD))
                .withdrawAmountUsd(rs.getBigDecimal(COL_WITHDRAW_AMOUNT_USD))
                .inoutCommissionUsd(rs.getBigDecimal(COL_INOUT_COMMISSION_USD))
                .transferAmountUsd(rs.getBigDecimal(COL_TRANSFER_AMOUNT_USD))
                .transferCommissionUsd(rs.getBigDecimal(COL_TRANSFER_COMMISSION_USD))
                .tradeAmountUsd(rs.getBigDecimal(COL_TRADE_AMOUNT_USD))
                .tradeCommissionUsd(rs.getBigDecimal(COL_TRADE_COMMISSION_USD))
                .balanceDynamicsUsd(rs.getBigDecimal(COL_BALANCE_DYNAMICS_USD))
                .sourceIds(getSourceIds(rs.getString(COL_SOURCE_IDS)))
                .build();
    }

    private List<Integer> getSourceIds(String ids) {
        String [] values = ids.split(",");
        return Arrays.stream(values)
                .filter(StringUtils::isNotBlank)
                .map(v -> Integer.parseInt(v.trim()))
                .collect(Collectors.toList());
    }
}
