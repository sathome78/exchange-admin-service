package me.exrates.adminservice.repository.impl;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.domain.User;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_JDBC_OPS;
import static me.exrates.adminservice.configurations.AdminDatasourceConfiguration.ADMIN_NP_TEMPLATE;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcOperations adminTemplate;
    private final JdbcOperations jdbcOperations;

    @Autowired
    public UserRepositoryImpl(@Qualifier(ADMIN_NP_TEMPLATE) NamedParameterJdbcOperations adminTemplate,
                              @Qualifier(ADMIN_JDBC_OPS) JdbcOperations jdbcOperations) {
        this.adminTemplate = adminTemplate;
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Optional<User> findOne(String username) throws UsernameNotFoundException {
        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE email = :username";
            MapSqlParameterSource params = new MapSqlParameterSource("username", username);
            return Optional.ofNullable(adminTemplate.queryForObject(sql, params, getRowMapper()));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean batchUpdate(List<CoreUser> users) {
        final String sql = "REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        final int[] rows = jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CoreUser user = users.get(i);
                ps.setInt(1, user.getUserId());
                ps.setString(2, user.getPublicId());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
                ps.setTimestamp(5, Timestamp.valueOf(user.getRegdate()));
                ps.setString(6, user.getPhone());
                ps.setString(7, user.getUserStatus());
                ps.setString(8, user.getUserRole());
                ps.setBoolean(9, user.isUse2fa());
                ps.setString(10, user.getKycStatus());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
        return rows.length == users.size();

    }

    private RowMapper<User> getRowMapper() {
        return (rs, i) -> User
                .builder()
                .id(rs.getLong(COL_USER_ID))
                .username(rs.getString(COL_EMAIL))
                .password(rs.getString(COL_PASSWORD))
                .userRole(UserRole.valueOf(rs.getString(COL_USER_ROLE)))
                .build();
    }
}
