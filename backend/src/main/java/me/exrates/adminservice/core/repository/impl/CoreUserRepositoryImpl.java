package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.domain.enums.UserStatus;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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
    public UserDashboardDto getUsersDashboard() {
        String sql = "SELECT u.id FROM USER u";

        final List<Integer> allUsers = coreTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        sql = "SELECT u.id FROM USER u WHERE u.kyc_status = 'SUCCESS' OR (u.kyc_status = 'ACCEPTED' AND u.kyc_verification_step = 2)";

        final List<Integer> allVerifiedUsers = coreTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        sql = "SELECT u.id FROM USER u WHERE u.status = 3";

        final List<Integer> allBlockedUsers = coreTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        return UserDashboardDto.builder()
                .allUsersCount(allUsers.size())
                .allVerifiedUsersCount(allVerifiedUsers.size())
                .allOnlineUsersCount(0) //todo: not implemented yet
                .allBlockedUsersCount(allBlockedUsers.size())
                .build();
    }

    @Override
    public List<UserInfoDto> getUserInfoList(FilterDto filter, int limit, int offset) {
        String sql = "SELECT " +
                "AGR.ip_address, " +
                "AGR.email, " +
                "AGR.country, " +
                "AGR.balance, " +
                "AGR.registration_date, " +
                "AGR.phone, " +
                "AGR.verification_status, " +
                "AGR.role_name, " +
                "AGR.status_id " +
                "FROM " +
                "(SELECT " +
                "u.ipaddress AS ip_address, " +
                "u.email, " +
                "u.country, " +
                "SUM((w.active_balance + w.reserved_balance) * ccr.usd_rate) AS balance, " +
                "u.regdate AS registration_date, " +
                "u.phone, " +
                "u.kyc_status AS verification_status, " +
                "u.kyc_verification_step AS verification_step, " +
                "ur.name AS role_name, " +
                "u.status AS status_id, " +
                "(SELECT COUNT(o.id) FROM EXORDERS o WHERE o.user_id = u.id AND o.status_id = 3) AS closed_orders, " +
                "(SELECT COUNT(rr.id) FROM REFILL_REQUEST rr WHERE rr.user_id = u.id AND rr.status_id IN (9, 10)) AS success_input, " +
                "(SELECT COUNT(wr.id) FROM WITHDRAW_REQUEST wr WHERE wr.user_id = u.id AND wr.status_id IN (9, 10)) AS success_output " +
                "FROM USER u " +
                "JOIN USER_ROLE ur ON ur.id = u.roleid AND ur.name IN ('USER') " +
                "JOIN EXORDERS o ON o.user_id = u.id AND o.status_id = 3 " +
                "JOIN WALLET w ON w.user_id = u.id " +
                "JOIN CURRENT_CURRENCY_RATES ccr ON ccr.currency_id = w.currency_id " +
                "JOIN CURRENCY cur ON cur.id = w.currency_id AND cur.name IN ('BTC') " +
                "GROUP BY w.user_id) AGR " +
                "WHERE " +
                "AGR.balance BETWEEN 0 AND 10000 AND " +
                "AGR.registration_date BETWEEN '2019-01-01 00:00' AND '2019-05-01 00:00' AND " +
                "AGR.verification_status = 'SUCCESS' OR (AGR.verification_status = 'ACCEPTED' AND AGR.verification_step = 2) AND " +
                "AGR.closed_orders BETWEEN 0 AND 1000 AND " +
                "AGR.success_input BETWEEN 0 AND 1000 AND " +
                "AGR.success_output BETWEEN 0 AND 1000 " +
                "LIMIT 15 OFFSET 0";

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_roles", "");
        }};

        return coreTemplate.query(sql, namedParameters, (rs, idx) -> UserInfoDto.builder()
                .registerIp(rs.getString("ip_address"))
                .email(rs.getString("email"))
                .country(rs.getString("country"))
                .balanceSumUsd(rs.getBigDecimal("balance"))
                .registrationDate(rs.getTimestamp("registration_date").toLocalDateTime())
                .lastEntryDate(LocalDateTime.MIN) //todo: not implemented yet
                .phone(rs.getString("phone"))
                .verificationStatus(rs.getString("verification_status"))
                .role(UserRole.valueOf(rs.getString("role_name")))
                .status(UserStatus.convert(rs.getInt("status_id")))
                .build());
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