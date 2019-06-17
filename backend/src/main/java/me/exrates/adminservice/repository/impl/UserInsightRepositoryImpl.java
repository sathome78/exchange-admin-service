package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.repository.UserInsightRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
@Log4j2
public class UserInsightRepositoryImpl implements UserInsightRepository {

    private static final int DEFAULT_LIMIT = 20;
    private static final String LIMIT_KEY = "limit";
    private static final String OFFSET_KEY = "offset";
    private static final String LAST_YEAR_CONDITION = " " + COL_CREATED + " >= CURRENT_TIMESTAMP - INTERVAL 1 YEAR";

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public UserInsightRepositoryImpl(@Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public List<UserInsight> findAll(int limit, int offset, Set<Integer> userIds) {
        String userIdsCondition = "";
        Map<String, Object> params = new HashMap<>();
        if (!userIds.isEmpty()) {
            userIdsCondition = " WHERE " + COL_USER_ID + " IN (:ids) ";
            params.put("ids", userIds);
        }
        String limitCondition = " LIMIT :" + LIMIT_KEY;
        params.put(LIMIT_KEY, DEFAULT_LIMIT);
        if (limit != DEFAULT_LIMIT && limit > 0) {
            params.put(LIMIT_KEY, limit);
        }
        String offsetCondition = "";
        if (offset > 0) {
            offsetCondition = "OFFSET :" + OFFSET_KEY;
            params.put(OFFSET_KEY, offset);
        }
        String intervalCondition = StringUtils.isEmpty(userIdsCondition)
                ? " WHERE" + LAST_YEAR_CONDITION
                : " AND" + LAST_YEAR_CONDITION;
        String sql = "SELECT * FROM " + TABLE + userIdsCondition + intervalCondition + limitCondition + offsetCondition;
        return namedParameterJdbcOperations.query(sql, params, getRowMapper());
    }

    @Override
    public List<UserInsight> findAll(Set<Integer> userIds) {
        return findAll(DEFAULT_LIMIT, 0, Collections.emptySet());
    }

    @Override
    public List<UserInsight> findAll(int limit, int offset) {
        return findAll(limit, offset, Collections.emptySet());
    }

    @Override
    public Set<UserInsight> findAllByUserId(int userId) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_USER_ID + " = :userId" + " AND" + LAST_YEAR_CONDITION;
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return new HashSet<>(namedParameterJdbcOperations.query(sql, params, getRowMapper()));
    }

    @Override
    public Set<Integer> getActiveUserIds() {
        String sql = "SELECT DISTINCT(" + COL_USER_ID + ") FROM " + TABLE;
        return namedParameterJdbcOperations.query(sql, Collections.emptyMap(), rs -> {
            final Set<Integer> userIds = new HashSet<>();
            while (rs.next()) {
                userIds.add(rs.getInt(COL_USER_ID));
            }
            return userIds;
        });
    }

    private RowMapper<UserInsight> getRowMapper() {
        return (rs, i) -> UserInsight.builder()
                .created(rs.getDate(COL_CREATED).toLocalDate())
                .userId(rs.getInt(COL_USER_ID))
                .rateBtcForOneUsd(rs.getBigDecimal(COL_RATE_BTC_FOR_ONE_USD))
                .refillAmountUsd(rs.getBigDecimal(COL_REFILL_AMOUNT_USD))
                .withdrawAmountUsd(rs.getBigDecimal(COL_WITHDRAW_AMOUNT_USD))
                .inoutCommissionUsd(rs.getBigDecimal(COL_INOUT_COMMISSION_USD))
                .transferInAmountUsd(rs.getBigDecimal(COL_TRANSFER_IN_AMOUNT_USD))
                .transferOutAmountUsd(rs.getBigDecimal(COL_TRANSFER_OUT_AMOUNT_USD))
                .transferCommissionUsd(rs.getBigDecimal(COL_TRANSFER_COMMISSION_USD))
                .tradeSellCount(rs.getInt(COL_TRADE_SELL_COUNT))
                .tradeBuyCount(rs.getInt(COL_TRADE_BUY_COUNT))
                .tradeAmountUsd(rs.getBigDecimal(COL_TRADE_AMOUNT_USD))
                .tradeCommissionUsd(rs.getBigDecimal(COL_TRADE_COMMISSION_USD))
                .balanceDynamicsUsd(rs.getBigDecimal(COL_BALANCE_DYNAMICS_USD))
                .sourceIds(toList(rs.getString(COL_SOURCE_IDS)))
                .build();
    }

    private List<Integer> toList(String value) {
        if (StringUtils.isEmpty(value)) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.stream(value.split(","))
                .map(s -> Integer.parseInt(s.trim()))
                .collect(Collectors.toList());
    }
}
