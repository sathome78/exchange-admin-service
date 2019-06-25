package me.exrates.adminservice.repository.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.ClosedOrder;
import me.exrates.adminservice.repository.ClosedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_JDBC_OPS;
import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
@Log4j2
public class ClosedOrderRepositoryImpl implements ClosedOrderRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final JdbcOperations jdbcOperations;

    @Autowired
    public ClosedOrderRepositoryImpl(@Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations namedParameterJdbcOperations,
                                     @Qualifier(ADMIN_JDBC_OPS) JdbcOperations jdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<Integer> findMaxId() {
        try {
            String sql = "SELECT MAX(" + COL_ID + ") FROM " + TABLE;
            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject(sql, Collections.emptyMap(), Integer.class));
        } catch (DataAccessException e) {
            log.warn("Failed to find max id in close orders", e);
            return Optional.of(-1);
        }
    }

    @Override
    public boolean batchInsert(List<ClosedOrder> orders) {
        final String sql = "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final int[] rows = jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ClosedOrder order = orders.get(i);
                ps.setInt(1, order.getId());
                ps.setString(2, order.getCurrencyPairName());
                ps.setInt(3, order.getCreatorId());
                ps.setInt(4, order.getAcceptorId());
                ps.setBigDecimal(5, order.getRate());
                ps.setBigDecimal(6, order.getAmountBase());
                ps.setBigDecimal(7, order.getAmountConvert());
                ps.setBigDecimal(8, order.getAmountUsd());
                ps.setDate(9, java.sql.Date.valueOf(order.getClosedDate()));
                ps.setString(10, order.getBaseType());
            }

            @Override
            public int getBatchSize() {
                return orders.size();
            }
        });
        return rows.length == orders.size();
    }

    @Override
    public Map<Integer, List<ClosedOrder>> findAllUserClosedOrders(Collection<Integer> userIds) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " +
                COL_USER_ID + " IN (:ids) OR " + COL_USER_ACCEPTOR_ID + " IN (:ids)";
        Map<String, Object> params = Collections.singletonMap("ids", userIds);
        Map<Integer, List<ClosedOrder>> closedOrders = Maps.newHashMap();
        final List<ClosedOrder> orders = namedParameterJdbcOperations.query(sql, params, getClosedOrderRowMapper());
        orders.forEach(order -> computeClosedOrders(closedOrders, order));
        return closedOrders;
    }

    private void computeClosedOrders(Map<Integer, List<ClosedOrder>> closedOrders, ClosedOrder order) {
        closedOrders.computeIfPresent(order.getCreatorId(), (k, list) -> {
            list.add(order);
            return list;
        });
        closedOrders.computeIfPresent(order.getAcceptorId(), (k, list) -> {
            list.add(order);
            return list;
        });
        closedOrders.putIfAbsent(order.getCreatorId(), Lists.newArrayList(order));
        closedOrders.putIfAbsent(order.getAcceptorId(), Lists.newArrayList(order));
    }

    private RowMapper<ClosedOrder> getClosedOrderRowMapper() {
        return (rs, i) -> ClosedOrder.builder()
                .id(rs.getInt(COL_ID))
                .currencyPairName(rs.getString(COL_CURRENCY_PAIR_NAME))
                .creatorId(rs.getInt(COL_USER_ID))
                .acceptorId(rs.getInt(COL_USER_ACCEPTOR_ID))
                .rate(rs.getBigDecimal(COL_RATE))
                .amountBase(rs.getBigDecimal(COL_AMOUNT_BASE))
                .amountConvert(rs.getBigDecimal(COL_AMOUNT_CONVERT))
                .amountUsd(rs.getBigDecimal(COL_AMOUNT_USD))
                .closedDate(rs.getDate(COL_CLOSED).toLocalDate())
                .baseType(rs.getString(COL_BASE_TYPE))
                .build();
    }
}
