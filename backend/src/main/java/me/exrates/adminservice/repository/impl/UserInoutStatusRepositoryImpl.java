package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.UserInoutStatus;
import me.exrates.adminservice.repository.UserInoutStatusRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
@Log4j2
public class UserInoutStatusRepositoryImpl implements UserInoutStatusRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public UserInoutStatusRepositoryImpl(@Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations operations) {
        this.namedParameterJdbcOperations = operations;
    }

    @Override
    public Map<Integer, UserInoutStatus> findAll(List<Integer> userIds) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_USER_ID + " IN (:ids)";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", userIds);
        return namedParameterJdbcOperations.query(sql, params, rs -> {
            Map<Integer, UserInoutStatus> values = new HashMap<>();
            while (rs.next()) {
                values.put(rs.getInt(COL_USER_ID), map(rs));
            }
            return values;
        });
    }

    private UserInoutStatus map(ResultSet rs) {
        try {
            return UserInoutStatus.builder()
                    .userId(rs.getInt(COL_USER_ID))
                    .refillAmountUsd(rs.getBigDecimal(COL_REFILL_AMOUNT))
                    .withdrawAmountUsd(rs.getBigDecimal(COL_WITHDRAW_AMOUNT))
                    .modified(rs.getTimestamp(COL_MODIFIED).toLocalDateTime())
                    .build();
        } catch (SQLException e) {
            log.warn("Failed to map rs to UserInoutStatus", e);
            return new UserInoutStatus();
        }
    }
}
