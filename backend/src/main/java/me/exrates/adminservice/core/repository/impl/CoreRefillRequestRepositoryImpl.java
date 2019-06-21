package me.exrates.adminservice.core.repository.impl;

import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
        String sql = "SELECT " + COL_USER_ID + ", COUNT(" + COL_ID + ") FROM " + TABLE +
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
}
