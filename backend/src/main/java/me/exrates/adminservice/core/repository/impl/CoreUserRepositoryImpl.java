package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.domain.enums.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static me.exrates.adminservice.utils.CollectionUtil.isNotEmpty;

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
                .allOnlineUsersCount(0) //TODO: not implemented yet
                .allBlockedUsersCount(allBlockedUsers.size())
                .build();
    }

    @Override
    public List<UserInfoDto> getUserInfoList(FilterDto filter, int limit, int offset) {
        String balanceClause = StringUtils.EMPTY;
        if (nonNull(filter.getMinBalance()) && nonNull(filter.getMaxBalance())) {
            balanceClause = "AGR.balance BETWEEN :min_balance AND :max_balance AND ";
        } else if (nonNull(filter.getMinBalance())) {
            balanceClause = "AGR.balance >= :min_balance AND ";
        } else if (nonNull(filter.getMaxBalance())) {
            balanceClause = "AGR.balance <= :max_balance AND ";
        }

        String registrationDateClause = StringUtils.EMPTY;
        if (nonNull(filter.getRegisteredFrom()) && nonNull(filter.getRegisteredTo())) {
            registrationDateClause = "AGR.registration_date BETWEEN :registered_from AND :registered_to AND ";
        } else if (nonNull(filter.getRegisteredFrom())) {
            registrationDateClause = "AGR.registration_date >= :registered_from AND ";
        } else if (nonNull(filter.getRegisteredTo())) {
            registrationDateClause = "AGR.registration_date <= :registered_to AND ";
        }

        String lastEntryDateClause = StringUtils.EMPTY;

        String verificationStatusClause = filter.isVerified()
                ? "AGR.verification_status = 'SUCCESS' OR (AGR.verification_status = 'ACCEPTED' AND AGR.verification_step = 2) AND "
                : StringUtils.EMPTY;

        String closedOrdersCountClause = StringUtils.EMPTY;
        if (nonNull(filter.getMinClosedOrders()) && nonNull(filter.getMaxClosedOrders())) {
            closedOrdersCountClause = "AGR.closed_orders BETWEEN :min_closed_orders AND :max_closed_orders AND ";
        } else if (nonNull(filter.getMinClosedOrders())) {
            closedOrdersCountClause = "AGR.closed_orders >= :min_closed_orders AND ";
        } else if (nonNull(filter.getMaxClosedOrders())) {
            closedOrdersCountClause = "AGR.closed_orders <= :max_closed_orders AND ";
        }

        String refillRequestCountClause = StringUtils.EMPTY;
        if (nonNull(filter.getMinRefillRequests()) && nonNull(filter.getMaxRefillRequests())) {
            refillRequestCountClause = "AGR.success_input BETWEEN :min_refill_requests AND :max_refill_requests AND ";
        } else if (nonNull(filter.getMinRefillRequests())) {
            refillRequestCountClause = "AGR.success_input >= :min_refill_requests AND ";
        } else if (nonNull(filter.getMaxRefillRequests())) {
            refillRequestCountClause = "AGR.success_input <= :max_refill_requests AND ";
        }

        String withdrawRequestCountClause = StringUtils.EMPTY;
        if (nonNull(filter.getMinWithdrawRequests()) && nonNull(filter.getMaxWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output BETWEEN :min_withdraw_requests AND :max_withdraw_requests ";
        } else if (nonNull(filter.getMinWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output >= :min_withdraw_requests ";
        } else if (nonNull(filter.getMaxWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output <= :max_withdraw_requests ";
        }

        String limitStr = limit < 1 ? StringUtils.EMPTY : String.format(" LIMIT %d ", limit);
        String offsetStr = offset < 1 ? StringUtils.EMPTY : String.format(" OFFSET %d ", offset);

        String sql = "SELECT " +
                "AGR.user_id, " +
                "AGR.user_nickname, " +
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
                "u.id AS user_id, " +
                "u.nickname AS user_nickname, " +
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
                "JOIN USER_ROLE ur ON ur.id = u.roleid " +
                (nonNull(filter.getRole()) ? "AND ur.name IN (:user_role) " : StringUtils.EMPTY) +
                "JOIN EXORDERS o ON o.user_id = u.id " +
                (filter.isActive() ? "AND o.status_id = 3 " : StringUtils.EMPTY) +
                "JOIN WALLET w ON w.user_id = u.id " +
                "JOIN CURRENT_CURRENCY_RATES ccr ON ccr.currency_id = w.currency_id " +
                "JOIN CURRENCY cur ON cur.id = w.currency_id " +
                (isNotEmpty(filter.getCurrencies()) ? "AND cur.name IN (:currencies) " : StringUtils.EMPTY) +
                "GROUP BY w.user_id) AGR " +
                "WHERE " +
                balanceClause +
                registrationDateClause +
                lastEntryDateClause +
                verificationStatusClause +
                closedOrdersCountClause +
                refillRequestCountClause +
                withdrawRequestCountClause +
                limitStr +
                offsetStr;

        Map<String, Object> params = new HashMap<>();
        if (nonNull(filter.getMinBalance()) && nonNull(filter.getMaxBalance())) {
            params.put("min_balance", filter.getMinBalance());
            params.put("max_balance", filter.getMaxBalance());
        } else if (nonNull(filter.getMinBalance())) {
            params.put("min_balance", filter.getMinBalance());
        } else if (nonNull(filter.getMaxBalance())) {
            params.put("max_balance", filter.getMaxBalance());
        }
        if (nonNull(filter.getRegisteredFrom()) && nonNull(filter.getRegisteredTo())) {
            params.put("registered_from", Date.valueOf(filter.getRegisteredFrom()));
            params.put("registered_to", Date.valueOf(filter.getRegisteredTo()));
        } else if (nonNull(filter.getRegisteredFrom())) {
            params.put("registered_from", Date.valueOf(filter.getRegisteredFrom()));
        } else if (nonNull(filter.getRegisteredTo())) {
            params.put("registered_to", Date.valueOf(filter.getRegisteredTo()));
        }
        if (nonNull(filter.getMinClosedOrders()) && nonNull(filter.getMaxClosedOrders())) {
            params.put("min_closed_orders", filter.getMinClosedOrders());
            params.put("max_closed_orders", filter.getMaxClosedOrders());
        } else if (nonNull(filter.getMinClosedOrders())) {
            params.put("min_closed_orders", filter.getMinClosedOrders());
        } else if (nonNull(filter.getMaxClosedOrders())) {
            params.put("max_closed_orders", filter.getMaxClosedOrders());
        }
        if (nonNull(filter.getMinRefillRequests()) && nonNull(filter.getMaxRefillRequests())) {
            params.put("min_refill_requests", filter.getMinRefillRequests());
            params.put("max_refill_requests", filter.getMaxRefillRequests());
        } else if (nonNull(filter.getMinRefillRequests())) {
            params.put("min_refill_requests", filter.getMinRefillRequests());
        } else if (nonNull(filter.getMaxRefillRequests())) {
            params.put("max_refill_requests", filter.getMaxRefillRequests());
        }
        if (nonNull(filter.getMinWithdrawRequests()) && nonNull(filter.getMaxWithdrawRequests())) {
            params.put("min_withdraw_requests", filter.getMinWithdrawRequests());
            params.put("max_withdraw_requests", filter.getMaxWithdrawRequests());
        } else if (nonNull(filter.getMinWithdrawRequests())) {
            params.put("min_withdraw_requests", filter.getMinWithdrawRequests());
        } else if (nonNull(filter.getMaxWithdrawRequests())) {
            params.put("max_withdraw_requests", filter.getMaxWithdrawRequests());
        }
        if (nonNull(filter.getRole())) {
            params.put("user_role", filter.getRole().name());
        }
        if (isNotEmpty(filter.getCurrencies())) {
            params.put("user_role", String.join(",", filter.getCurrencies()));
        }

        return coreTemplate.query(sql, params, getUserInfoDtoRowMapper());
    }

    @Override
    public UserInfoDto getUserInfo(int userId) {
        String sql = "SELECT " +
                "AGR.user_id, " +
                "AGR.user_nickname, " +
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
                "u.id AS user_id, " +
                "u.nickname AS user_nickname, " +
                "u.ipaddress AS ip_address, " +
                "u.email, " +
                "u.country, " +
                "SUM((w.active_balance + w.reserved_balance) * ccr.usd_rate) AS balance, " +
                "u.regdate AS registration_date, " +
                "u.phone, " +
                "u.kyc_status AS verification_status, " +
                "u.kyc_verification_step AS verification_step, " +
                "ur.name AS role_name, " +
                "u.status AS status_id " +
                "FROM USER u " +
                "JOIN USER_ROLE ur ON ur.id = u.roleid " +
                "JOIN WALLET w ON w.user_id = u.id " +
                "JOIN CURRENT_CURRENCY_RATES ccr ON ccr.currency_id = w.currency_id " +
                "GROUP BY w.user_id) AGR " +
                "WHERE AGR.user_id = :user_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        try {
            return coreTemplate.queryForObject(sql, params, getUserInfoDtoRowMapper());
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Do not found user: %d", userId));
        }
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

    private RowMapper<UserInfoDto> getUserInfoDtoRowMapper() {
        return (rs, idx) -> UserInfoDto.builder()
                .userId(rs.getInt("user_id"))
                .userNickname(rs.getString("user_nickname"))
                .registerIp(rs.getString("ip_address"))
                .email(rs.getString("email"))
                .country(rs.getString("country"))
                .balanceSumUsd(rs.getBigDecimal("balance"))
                .registrationDate(rs.getTimestamp("registration_date").toLocalDateTime())
                .lastEntryDate(LocalDateTime.MIN) //TODO: not implemented yet
                .phone(rs.getString("phone"))
                .verificationStatus(rs.getString("verification_status"))
                .role(UserRole.valueOf(rs.getString("role_name")))
                .status(UserStatus.convert(rs.getInt("status_id")))
                .build();
    }
}