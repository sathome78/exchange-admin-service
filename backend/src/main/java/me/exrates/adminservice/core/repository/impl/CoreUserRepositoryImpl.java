package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@Log4j2
public class CoreUserRepositoryImpl implements CoreUserRepository {

    private final NamedParameterJdbcOperations coreTemplate;

    private static final String USER_SELECT_SQL = "SELECT u.id, u.pub_id, u.email, u.password, u.regdate, u.phone,  " +
            " UPPER(US.name) as status, UPPER(UR.name) AS user_role, u.use2fa, u.kyc_status" +
            " FROM USER u" +
            " LEFT JOIN USER_ROLE UR on u.roleid = UR.id" +
            " LEFT JOIN USER_STATUS US on u.status = US.id ";

    @Autowired
    public CoreUserRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreTemplate) {
        this.coreTemplate = coreTemplate;
    }

    @Override
    public List<CoreUser> findAllAdmins() {
        String sql = USER_SELECT_SQL + " WHERE u.roleid = 1 AND u.status = 2";
        return coreTemplate.query(sql, Collections.emptyMap(), getCoreUserRowMapper());
    }

    @Override
    public Optional<CoreUser> findById(int userId) {
        try {
            String sql = USER_SELECT_SQL + " WHERE u.id = :userId";
            MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
            return Optional.ofNullable(coreTemplate.queryForObject(sql, params, getCoreUserRowMapper()));
        } catch (DataAccessException e) {
            log.debug("Failed to find user with id: " + userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<CoreUser> findByUsername(String username) {
        try {
            String sql = USER_SELECT_SQL + " WHERE u.email = :username";
            MapSqlParameterSource params = new MapSqlParameterSource("username", username);
            return Optional.ofNullable(coreTemplate.queryForObject(sql, params, getCoreUserRowMapper()));
        } catch (DataAccessException e) {
            log.debug("Failed to find user with email: " + username, e);
            return Optional.empty();
        }
    }

    @Override
    public Map<Integer, String> findAllUsersIdAndEmail() {
        String sql = "SELECT u.id, u.email FROM USER u";
        return coreTemplate.query(sql, Collections.emptyMap(), rs -> {
            Map<Integer, String> users = new HashMap<>();
            while (rs.next()) {
                users.put(rs.getInt("id"), rs.getString("email"));
            }
            return users;
        });
    }

    @Override
    public Collection<Integer> getBotsIds() {
        String sql = "SELECT u.id FROM USER u" +
                " WHERE u.roleid IN (SELECT ur.id FROM USER_ROLE ur WHERE ur.name IN (\'BOT_TRADER\', \'OUTER_MARKET_BOT\'))";
        return coreTemplate.query(sql, Collections.emptyMap(), (rs, rowNum) -> rs.getInt(1));
    }

    private RowMapper<CoreUser> getCoreUserRowMapper() {
        return (rs, i) -> CoreUser.builder()
                .userId(rs.getInt(CoreUserRepository.COL_USER_ID))
                .publicId(rs.getString(COL_PUBLIC_ID))
                .email(rs.getString(COL_EMAIL))
                .password(rs.getString(COL_PASSWORD))
                .regdate(Objects.isNull(rs.getTimestamp(COL_REG_DATE)) ? null : rs.getTimestamp(COL_REG_DATE).toLocalDateTime())
                .phone(rs.getString(COL_PHONE))
                .userStatus(rs.getString(COL_USER_STATUS))
                .userRole(rs.getString(COL_USER_ROLE))
                .use2fa(rs.getBoolean(COL_IS_2FA_ENABLED))
                .kycStatus(rs.getString(COL_KYC_STATUS))
                .build();
    }
}
