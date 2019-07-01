package me.exrates.adminservice.core.repository.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.OrderBaseType;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import me.exrates.adminservice.domain.ClosedOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Repository
public class CoreOrderRepositoryImpl implements CoreOrderRepository {

    private static final String BUY = "buy";
    private static final String SELL = "sell";

    private final NamedParameterJdbcOperations npJdbcOperations;

    @Autowired
    public CoreOrderRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations npJdbcOperations) {
        this.npJdbcOperations = npJdbcOperations;
    }

    @Override
    public List<ClosedOrder> findAllLimited(int chunkSize, int maxId) {
        String sql = "SELECT E.id, CP.name, E.user_id, E.user_acceptor_id, E.exrate, E.amount_base, E.amount_convert," +
                " DATE(E.date_acception) as closed, E.base_type" +
                " FROM EXORDERS E" +
                " LEFT JOIN CURRENCY_PAIR CP on E.currency_pair_id = CP.id" +
                " WHERE E.status_id = 3 AND E.user_id <> E.user_acceptor_id" +
                " AND E.id > :position" +
                " ORDER BY E.id ASC" +
                " LIMIT :size";
        Map<String, Object> params = Maps.newHashMap();
        params.put("size", chunkSize);
        params.put("position", maxId);
        return npJdbcOperations.query(sql, params, getRowMapper());
    }

    @Override
    public Map<String, Integer> getDailyBuySellVolume() {
        String sql = "SELECT" +
                " SUM(CASE WHEN " + COL_OPERATION_TYPE_ID + " = 3 THEN " + COL_AMOUNT_BASE + " ELSE 0 END) AS " + SELL + "," +
                " SUM(CASE WHEN " + COL_OPERATION_TYPE_ID + " = 4 THEN " + COL_AMOUNT_BASE + " ELSE 0 END) AS " + BUY +
                " FROM " + TABLE +
                " WHERE " + COL_STATUS_ID + " = 3" +
                " AND " + COL_DATE_ACCEPTION + " > CURRENT_TIMESTAMP - INTERVAL 1 DAY;";
        final Map<String, BigDecimal> rawValues = npJdbcOperations.query(sql, Collections.emptyMap(), rs -> {
            final Map<String, BigDecimal> values = new HashMap<>(2);
            while (rs.next()) {
                values.put(BUY, Objects.nonNull(rs.getBigDecimal(BUY)) ? rs.getBigDecimal(BUY) : BigDecimal.ZERO);
                values.put(SELL, Objects.nonNull(rs.getBigDecimal(SELL)) ? rs.getBigDecimal(SELL) : BigDecimal.ZERO);
            }
            return values;
        });
        return getPercentage(rawValues);
    }

    @Override
    public int getDailyUniqueUsersQuantity() {
        String sql = "SELECT " + COL_USER_ID + ", " + COL_USER_ACCEPTOR_ID +
                " FROM " + TABLE +
                " WHERE " + COL_STATUS_ID + " = 3" +
                " AND " + COL_DATE_ACCEPTION + " > CURRENT_TIMESTAMP - INTERVAL 1 DAY;";
        final Set<Integer> uniqUsers = npJdbcOperations.query(sql, Collections.emptyMap(), rs -> {
            final Set<Integer> users = new HashSet<>();
            while (rs.next()) {
                users.add(rs.getInt(COL_USER_ID));
                users.add(rs.getInt(COL_USER_ACCEPTOR_ID));
            }
            return users;
        });
        return uniqUsers.size();
    }

    @Override
    public CoreOrderDto findOrderById(int id) {
        final String sql = "SELECT " +
                "o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.user_id, " +
                "o.user_acceptor_id, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.amount_base, " +
                "o.amount_convert, " +
                "o.operation_type_id, " +
                "o.base_type " +
                "FROM EXORDERS o WHERE o.id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        try {
            return npJdbcOperations.queryForObject(sql, params, getCoreOrderDtoRowMapper());
        } catch (Exception ex) {
            log.warn("Failed to find order by id: {}", id);
            return null;
        }
    }

    private RowMapper<CoreOrderDto> getCoreOrderDtoRowMapper() {
        return (rs, idx) -> CoreOrderDto.builder()
                .id(rs.getInt("order_id"))
                .userId(rs.getInt("user_id"))
                .currencyPairId(rs.getInt("currency_pair_id"))
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .amountBase(rs.getBigDecimal("amount_base"))
                .amountConvert(rs.getBigDecimal("amount_convert"))
                .comissionId(rs.getInt("commission_id"))
                .commissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"))
                .userAcceptorId(rs.getInt("user_acceptor_id"))
                .orderBaseType(OrderBaseType.convert(rs.getString("base_type")))
                .build();
    }

    @VisibleForTesting
    protected Map<String, Integer> getPercentage(Map<String, BigDecimal> rawValues) {
        Map<String, Integer> result = new HashMap<>(2);
        if (rawValues.isEmpty() || rawValues.values().stream().allMatch(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) < 1)) {
            result.put(BUY, 0);
            result.put(SELL, 0);
            return result;
        }
        BigDecimal total = rawValues.getOrDefault(BUY, BigDecimal.ZERO).add(rawValues.getOrDefault(SELL, BigDecimal.ZERO));
        int buyPercent = rawValues.getOrDefault(BUY, BigDecimal.ZERO).intValue() * 100 / total.intValue();
        result.put(BUY, buyPercent);
        result.put(SELL, 100 - buyPercent);
        return result;
    }

    private RowMapper<ClosedOrder> getRowMapper() {
        return (rs, i) -> ClosedOrder.builder()
                .id(rs.getInt("id"))
                .currencyPairName(rs.getString("name"))
                .creatorId(rs.getInt("user_id"))
                .acceptorId(rs.getInt("user_acceptor_id"))
                .rate(rs.getBigDecimal("exrate"))
                .amountBase(rs.getBigDecimal("amount_base"))
                .amountConvert(rs.getBigDecimal("amount_convert"))
                .closedDate(rs.getDate("closed").toLocalDate())
                .baseType(rs.getString("base_type"))
                .build();
    }
}