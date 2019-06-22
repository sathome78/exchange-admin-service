package me.exrates.adminservice.core.repository.impl;

import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.exrates.adminservice.configurations.CoreDatasourceConfiguration.CORE_NP_TEMPLATE;

@Repository
public class CoreRefillRequestRepositoryImpl implements CoreRefillRequestRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public CoreRefillRequestRepositoryImpl(@Qualifier(CORE_NP_TEMPLATE) NamedParameterJdbcOperations operations) {
        this.namedParameterJdbcOperations = operations;
    }

    @Override
    public Map<Integer, Integer> getRefillAddressGeneratedByUserIds(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String sql = "SELECT " + COL_USER_ID + ", COUNT(DISTINCT(" + COL_CURRENCY_ID + ")) FROM " + TABLE +
                " WHERE " + COL_USER_ID + " IN (:ids)" +
                " GROUP BY " + COL_USER_ID;
        Map<String, Object> params = new HashMap<>();
        params.put("ids", userIds);
        final Map<Integer, Integer> result = namedParameterJdbcOperations.query(sql, params, rs -> {
            Map<Integer, Integer> values = new HashMap<>();
            while (rs.next()) {
                values.put(rs.getInt(COL_USER_ID), rs.getInt(2));
            }
            return values;
        });
        userIds.forEach(id -> result.putIfAbsent(id, 0));
        return result;
    }

    @Override
    public List<Pair<Integer, LocalDateTime>> findGeneratedUnpaidAddressesByUserIds(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        String sql = "SELECT rrr.user_id, rrr.date_generation" +
                " FROM REFILL_REQUEST_ADDRESS rrr" +
                " LEFT JOIN refill_request r1 on rrr.user_id = r1.user_id and rrr.id = r1.refill_request_address_id" +
                " WHERE r1.status_id NOT IN (9, 10)" +
                " AND rrr.date_generation - INTERVAL 90 DAY" +
                " AND rrr.user_id IN (:ids)" +
                " UNION" +
                " SELECT r2.user_id, r2.date_creation" +
                " FROM REFILL_REQUEST r2" +
                " WHERE r2.status_id IN (4)" +
                "  AND r2.refill_request_address_id IS NULL" +
                "  AND r2.date_creation - INTERVAL 90 DAY" +
                "  AND r2.user_id IN (:ids)";
        Map<String, Object> params = new HashMap<>(1);
        params.put("ids", userIds);
        return namedParameterJdbcOperations.query(sql, params, rs -> {
            List<Pair<Integer, LocalDateTime>> records = new ArrayList<>();
            while (rs.next()) {
                records.add(ImmutablePair.of(rs.getInt("user_id"), rs.getTimestamp("date_generation").toLocalDateTime()));
            }
            return records;
        });
    }
}
