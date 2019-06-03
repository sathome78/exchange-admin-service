package me.exrates.adminservice.repository.impl;

import me.exrates.adminservice.domain.User;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String TABLE = "USER";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_REG_DATE = "regdate";
    private static final String COL_PHONE = "phone";
    private static final String COL_USER_STATUS = "user_status";
    private static final String COL_USER_ROLE = "user_role";
    private static final String COL_IS_2FA_ENABLED = "use2fa";
    private static final String COL_KYC_STATUS = "kyc_status";

    private final NamedParameterJdbcOperations adminTemplate;

    @Autowired
    public UserRepositoryImpl(@Qualifier("adminNPTemplate") NamedParameterJdbcOperations adminTemplate) {
        this.adminTemplate = adminTemplate;
    }

    @Override
    public Optional<User> findOne(String username) throws UsernameNotFoundException {
        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE email =: username";
            MapSqlParameterSource params = new MapSqlParameterSource("username", username);
            return Optional.ofNullable(adminTemplate.queryForObject(sql, params, getRowMapper()));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
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
