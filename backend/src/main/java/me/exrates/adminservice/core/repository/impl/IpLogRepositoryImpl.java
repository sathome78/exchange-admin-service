package me.exrates.adminservice.core.repository.impl;

import com.google.common.collect.Maps;
import me.exrates.adminservice.core.repository.IpLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static me.exrates.adminservice.configurations.CoreDatasourceConfiguration.CORE_NP_TEMPLATE;

@Repository
public class IpLogRepositoryImpl implements IpLogRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public IpLogRepositoryImpl(@Qualifier(CORE_NP_TEMPLATE) NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public Map<Integer, LocalDateTime> findAllByUserIds(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Maps.newHashMap();
        }
        String sql = "SELECT " + COL_USER_ID + ", MAX(" + COL_DATE + ") as last_login" +
                " FROM " + TABLE +
                " WHERE " + COL_USER_ID + " IN (:ids) AND " + COL_EVENT + " = \'LOGIN_SUCCESS\'" +
                " GROUP BY " + COL_USER_ID;
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", userIds);
        return namedParameterJdbcOperations.query(sql, params, rs -> {
            Map<Integer, LocalDateTime> results = Maps.newHashMap();
            while (rs.next()) {
                results.put(rs.getInt(COL_USER_ID), rs.getTimestamp("last_login").toLocalDateTime());
            }
            return results;
        });
    }
}
