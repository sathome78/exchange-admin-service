package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.ReferralTransactionDto;
import me.exrates.adminservice.core.domain.enums.UserOperationAuthority;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.domain.enums.UserStatus;
import me.exrates.adminservice.core.exceptions.UserNotFoundException;
import me.exrates.adminservice.core.exceptions.UserRoleNotFoundException;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static me.exrates.adminservice.configurations.CoreDatasourceConfiguration.CORE_NP_TEMPLATE;
import static me.exrates.adminservice.configurations.CoreDatasourceConfiguration.CORE_TEMPLATE;
import static me.exrates.adminservice.utils.CollectionUtil.isNotEmpty;

@Repository
@Log4j2
public class CoreUserRepositoryImpl implements CoreUserRepository {

    private final NamedParameterJdbcOperations coreNPTemplate;
    private final JdbcOperations coreTemplate;

    private static final String USER_SELECT_SQL = "SELECT u.id, u.pub_id, u.email, u.password, u.regdate, u.phone,  " +
            " UPPER(US.name) as status, UPPER(UR.name) AS user_role, u.use2fa, u.kyc_status" +
            " FROM USER u" +
            " LEFT JOIN USER_ROLE UR on u.roleid = UR.id" +
            " LEFT JOIN USER_STATUS US on u.status = US.id ";

    @Autowired
    public CoreUserRepositoryImpl(@Qualifier(CORE_NP_TEMPLATE) NamedParameterJdbcOperations coreNPTemplate,
                                  @Qualifier(CORE_TEMPLATE) JdbcOperations coreTemplate) {
        this.coreNPTemplate = coreNPTemplate;
        this.coreTemplate = coreTemplate;
    }

    @Override
    public List<CoreUser> findAllAdmins() {
        String sql = USER_SELECT_SQL + " WHERE u.roleid = 1 AND u.status = 2";
        return coreNPTemplate.query(sql, Collections.emptyMap(), getCoreUserRowMapper());
    }

    @Override
    public Optional<CoreUser> findById(int userId) {
        try {
            String sql = USER_SELECT_SQL + " WHERE u.id = :userId";
            MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
            return Optional.ofNullable(coreNPTemplate.queryForObject(sql, params, getCoreUserRowMapper()));
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
            return Optional.ofNullable(coreNPTemplate.queryForObject(sql, params, getCoreUserRowMapper()));
        } catch (DataAccessException e) {
            log.debug("Failed to find user with email: " + username, e);
            return Optional.empty();
        }
    }

    @Override
    public Integer getIdByEmail(String email) {
        final String sql = "SELECT u.id FROM USER u WHERE u.email = :email";

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);

        try {
            return coreNPTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception ex) {
            throw new UserNotFoundException(String.format("User: %s not found", email));
        }
    }

    @Override
    public Map<Integer, String> findAllUsersIdAndEmail() {
        String sql = "SELECT u.id, u.email FROM USER u";
        return coreNPTemplate.query(sql, Collections.emptyMap(), rs -> {
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
        return coreNPTemplate.query(sql, Collections.emptyMap(), (rs, rowNum) -> rs.getInt(1));
    }

    @Override
    public UserRole getUserRoleById(Integer userId) {
        final String sql = "SELECT ur.name as role_name " +
                "FROM USER u " +
                "JOIN USER_ROLE ur on ur.id = u.roleid " +
                "WHERE u.id = :user_id ";

        try {
            return coreNPTemplate.queryForObject(sql, Collections.singletonMap("user_id", userId), (rs, row) -> UserRole.valueOf(rs.getString("role_name")));
        } catch (Exception ex) {
            throw new UserRoleNotFoundException(String.format("User role for user: %d not found", userId));
        }
    }

    @Override
    public UserDashboardDto getUsersDashboard() {
        String sql = "SELECT u.id FROM USER u";

        final List<Integer> allUsers = coreNPTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        sql = "SELECT u.id FROM USER u WHERE u.kyc_status = \'SUCCESS\' OR (u.kyc_status = \'ACCEPTED\' AND u.kyc_verification_step = 2)";

        final List<Integer> allVerifiedUsers = coreNPTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        sql = "SELECT MAX(aut.expired_at) FROM API_AUTH_TOKEN aut WHERE aut.expired_at > NOW() GROUP BY aut.username";

        final List<Integer> allOnlineUsers = coreNPTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        sql = "SELECT u.id FROM USER u WHERE u.status = 3";

        final List<Integer> allBlockedUsers = coreNPTemplate.queryForList(sql, Collections.emptyMap(), Integer.class);

        return UserDashboardDto.builder()
                .allUsersCount(allUsers.size())
                .allVerifiedUsersCount(allVerifiedUsers.size())
                .allOnlineUsersCount(allOnlineUsers.size())
                .allBlockedUsersCount(allBlockedUsers.size())
                .build();
    }

    @Override
    public Integer getUserInfoListCount(FilterDto filter, Integer limit, Integer offset) {
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

        String lastLoginDateClause = StringUtils.EMPTY;
        if (nonNull(filter.getLastLoginFrom()) && nonNull(filter.getLastLoginTo())) {
            lastLoginDateClause = "AGR.last_login_date BETWEEN :last_login_from AND :last_login_to AND ";
        } else if (nonNull(filter.getLastLoginFrom())) {
            lastLoginDateClause = "AGR.last_login_date >= :last_login_from AND ";
        } else if (nonNull(filter.getLastLoginTo())) {
            lastLoginDateClause = "AGR.last_login_date <= :last_login_to AND ";
        }

        String verificationStatusClause = filter.isVerified()
                ? "AGR.verification_status = \'SUCCESS\' OR (AGR.verification_status = \'ACCEPTED\' AND AGR.verification_step = 2) AND "
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
            withdrawRequestCountClause = "AGR.success_output BETWEEN :min_withdraw_requests AND :max_withdraw_requests AND ";
        } else if (nonNull(filter.getMinWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output >= :min_withdraw_requests AND ";
        } else if (nonNull(filter.getMaxWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output <= :max_withdraw_requests AND ";
        }

        String userStatusClause = filter.isActive() ? "AGR.status_id = 2 " : "AGR.status_id = 3 ";

        String limitStr = limit < 1 ? StringUtils.EMPTY : String.format(" LIMIT %d ", limit);
        String offsetStr = offset < 1 ? StringUtils.EMPTY : String.format(" OFFSET %d ", offset);

        String sql = "SELECT " +
                "COUNT(AGR.user_id) AS records_count " +
                "FROM " +
                "(SELECT " +
                "u.id AS user_id, " +
                "u.status AS status_id, " +
                "SUM((w.active_balance + w.reserved_balance) * ccr.usd_rate) AS balance, " +
                "u.regdate AS registration_date, " +
                "(SELECT MAX(ipl.date) " +
                " FROM IP_Log ipl " +
                " WHERE ipl.user_id = u.id AND ipl.event = \'LOGIN_SUCCESS\' " +
                " GROUP BY ipl.user_id) AS last_login_date, " +
                "u.kyc_status AS verification_status, " +
                "u.kyc_verification_step AS verification_step, " +
                "(SELECT COUNT(o.id) " +
                " FROM EXORDERS o " +
                " WHERE o.user_id = u.id AND o.status_id = 3) AS closed_orders, " +
                "(SELECT COUNT(rr.id) " +
                " FROM REFILL_REQUEST rr " +
                " WHERE rr.user_id = u.id AND rr.status_id IN (9, 10)) AS success_input, " +
                "(SELECT COUNT(wr.id) " +
                " FROM WITHDRAW_REQUEST wr " +
                " WHERE wr.user_id = u.id AND wr.status_id IN (9, 10)) AS success_output " +
                "FROM USER u " +
                "JOIN USER_ROLE ur ON ur.id = u.roleid " +
                (nonNull(filter.getRole()) ? "AND ur.name IN (:user_role) " : StringUtils.EMPTY) +
                "JOIN EXORDERS o ON o.user_id = u.id AND o.status_id = 3 " +
                "JOIN WALLET w ON w.user_id = u.id " +
                "JOIN CURRENT_CURRENCY_RATES ccr ON ccr.currency_id = w.currency_id " +
                "JOIN CURRENCY cur ON cur.id = w.currency_id " +
                (isNotEmpty(filter.getCurrencyNames()) ? "AND cur.name IN (:currencies) " : StringUtils.EMPTY) +
                "GROUP BY w.user_id) AGR " +
                "WHERE " +
                balanceClause +
                registrationDateClause +
                lastLoginDateClause +
                verificationStatusClause +
                closedOrdersCountClause +
                refillRequestCountClause +
                withdrawRequestCountClause +
                userStatusClause +
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
        if (nonNull(filter.getLastLoginFrom()) && nonNull(filter.getLastLoginTo())) {
            params.put("last_login_from", Date.valueOf(filter.getLastLoginFrom()));
            params.put("last_login_to", Date.valueOf(filter.getLastLoginTo()));
        } else if (nonNull(filter.getLastLoginFrom())) {
            params.put("last_login_from", Date.valueOf(filter.getLastLoginFrom()));
        } else if (nonNull(filter.getLastLoginTo())) {
            params.put("last_login_to", Date.valueOf(filter.getLastLoginTo()));
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
        if (isNotEmpty(filter.getCurrencyNames())) {
            params.put("currencies", String.join(",", filter.getCurrencyNames()));
        }

        try {
            return coreNPTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception ex) {
            return 0;
        }
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

        String lastLoginDateClause = StringUtils.EMPTY;
        if (nonNull(filter.getLastLoginFrom()) && nonNull(filter.getLastLoginTo())) {
            lastLoginDateClause = "AGR.last_login_date BETWEEN :last_login_from AND :last_login_to AND ";
        } else if (nonNull(filter.getLastLoginFrom())) {
            lastLoginDateClause = "AGR.last_login_date >= :last_login_from AND ";
        } else if (nonNull(filter.getLastLoginTo())) {
            lastLoginDateClause = "AGR.last_login_date <= :last_login_to AND ";
        }

        String verificationStatusClause = filter.isVerified()
                ? "AGR.verification_status = \'SUCCESS\' OR (AGR.verification_status = \'ACCEPTED\' AND AGR.verification_step = 2) AND "
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
            withdrawRequestCountClause = "AGR.success_output BETWEEN :min_withdraw_requests AND :max_withdraw_requests AND ";
        } else if (nonNull(filter.getMinWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output >= :min_withdraw_requests AND ";
        } else if (nonNull(filter.getMaxWithdrawRequests())) {
            withdrawRequestCountClause = "AGR.success_output <= :max_withdraw_requests AND ";
        }

        String userStatusClause = filter.isActive() ? "AGR.status_id = 2 " : "AGR.status_id = 3 ";

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
                "AGR.last_login_date, " +
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
                "(SELECT MAX(ipl.date) " +
                " FROM IP_Log ipl " +
                " WHERE ipl.user_id = u.id AND ipl.event = \'LOGIN_SUCCESS\' " +
                " GROUP BY ipl.user_id) AS last_login_date, " +
                "u.phone, " +
                "u.kyc_status AS verification_status, " +
                "u.kyc_verification_step AS verification_step, " +
                "ur.name AS role_name, " +
                "u.status AS status_id, " +
                "(SELECT COUNT(o.id) " +
                " FROM EXORDERS o " +
                " WHERE o.user_id = u.id AND o.status_id = 3) AS closed_orders, " +
                "(SELECT COUNT(rr.id) " +
                " FROM REFILL_REQUEST rr " +
                " WHERE rr.user_id = u.id AND rr.status_id IN (9, 10)) AS success_input, " +
                "(SELECT COUNT(wr.id) " +
                " FROM WITHDRAW_REQUEST wr " +
                " WHERE wr.user_id = u.id AND wr.status_id IN (9, 10)) AS success_output " +
                "FROM USER u " +
                "JOIN USER_ROLE ur ON ur.id = u.roleid " +
                (nonNull(filter.getRole()) ? "AND ur.name IN (:user_role) " : StringUtils.EMPTY) +
                "JOIN EXORDERS o ON o.user_id = u.id AND o.status_id = 3 " +
                "JOIN WALLET w ON w.user_id = u.id " +
                "JOIN CURRENT_CURRENCY_RATES ccr ON ccr.currency_id = w.currency_id " +
                "JOIN CURRENCY cur ON cur.id = w.currency_id " +
                (isNotEmpty(filter.getCurrencyNames()) ? "AND cur.name IN (:currencies) " : StringUtils.EMPTY) +
                "GROUP BY w.user_id) AGR " +
                "WHERE " +
                balanceClause +
                registrationDateClause +
                lastLoginDateClause +
                verificationStatusClause +
                closedOrdersCountClause +
                refillRequestCountClause +
                withdrawRequestCountClause +
                userStatusClause +
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
        if (nonNull(filter.getLastLoginFrom()) && nonNull(filter.getLastLoginTo())) {
            params.put("last_login_from", Date.valueOf(filter.getLastLoginFrom()));
            params.put("last_login_to", Date.valueOf(filter.getLastLoginTo()));
        } else if (nonNull(filter.getLastLoginFrom())) {
            params.put("last_login_from", Date.valueOf(filter.getLastLoginFrom()));
        } else if (nonNull(filter.getLastLoginTo())) {
            params.put("last_login_to", Date.valueOf(filter.getLastLoginTo()));
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
        if (isNotEmpty(filter.getCurrencyNames())) {
            params.put("currencies", String.join(",", filter.getCurrencyNames()));
        }

        return coreNPTemplate.query(sql, params, getUserInfoDtoRowMapper());
    }

    @Override
    public UserInfoDto getUserInfo(int userId) {
        final String sql = "SELECT " +
                "AGR.user_id, " +
                "AGR.user_nickname, " +
                "AGR.ip_address, " +
                "AGR.email, " +
                "AGR.country, " +
                "AGR.balance, " +
                "AGR.registration_date, " +
                "AGR.last_login_date, " +
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
                "(SELECT MAX(ipl.date) " +
                " FROM IP_Log ipl " +
                " WHERE ipl.user_id = u.id AND ipl.event = \'LOGIN_SUCCESS\' " +
                " GROUP BY ipl.user_id) AS last_login_date, " +
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
            return coreNPTemplate.queryForObject(sql, params, getUserInfoDtoRowMapper());
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Do not found user info (user id: %d)", userId));
        }
    }

    @Override
    public Integer getUserBalancesInfoListCount(int userId, boolean withoutZeroBalances, List<String> currencyNames) {
        String sql = "SELECT COUNT(w.id) AS records_count " +
                "FROM WALLET w " +
                "JOIN CURRENCY cur ON cur.id = w.currency_id " +
                (isNotEmpty(currencyNames) ? "AND cur.name IN (:currencies) " : StringUtils.EMPTY) +
                "WHERE w.user_id = :user_id " +
                (withoutZeroBalances ? "AND IFNULL(w.active_balance + w.reserved_balance, 0) > 0 " : StringUtils.EMPTY);

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        if (isNotEmpty(currencyNames)) {
            params.put("currencies", String.join(",", currencyNames));
        }

        try {
            return coreNPTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public List<UserBalancesInfoDto> getUserBalancesInfoList(int userId, boolean withoutZeroBalances, List<String> currencyNames, int limit, int offset) {
        String limitStr = limit < 1 ? StringUtils.EMPTY : String.format(" LIMIT %d ", limit);
        String offsetStr = offset < 1 ? StringUtils.EMPTY : String.format(" OFFSET %d ", offset);

        String sql = "SELECT " +
                "cur.name AS currency_name, " +
                "(SELECT rra.address " +
                " FROM REFILL_REQUEST rr " +
                " JOIN REFILL_REQUEST_ADDRESS rra ON rra.id = rr.refill_request_address_id " +
                " WHERE rr.user_id = w.user_id AND rr.currency_id = cur.id AND rr.status_id IN (9, 10) " +
                " ORDER BY rr.status_modification_date DESC " +
                " LIMIT 1) AS last_refill_address, " +
                "IFNULL((SELECT SUM(rr.amount) AS refill_sum " +
                " FROM REFILL_REQUEST rr " +
                " WHERE rr.user_id = w.user_id AND rr.currency_id = cur.id AND rr.status_id IN (9, 10)), 0) AS all_refill_sum, " +
                "(SELECT wr.wallet " +
                " FROM WITHDRAW_REQUEST wr " +
                " WHERE wr.user_id = w.user_id AND wr.currency_id = cur.id AND wr.status_id IN (9, 10) " +
                " ORDER BY wr.date_creation DESC " +
                " LIMIT 1) AS last_withdraw_address, " +
                "IFNULL((SELECT SUM(wr.amount) AS withdraw_sum " +
                " FROM WITHDRAW_REQUEST wr " +
                " WHERE wr.user_id = w.user_id AND wr.currency_id = cur.id AND wr.status_id IN (9, 10)), 0) AS all_withdraw_sum, " +
                "IFNULL(w.active_balance, 0) AS active_balance, " +
                "IFNULL(w.reserved_balance, 0) AS reserved_balance, " +
                "IFNULL(w.active_balance + w.reserved_balance, 0) AS common_balance " +
                "FROM WALLET w " +
                "JOIN CURRENCY cur ON cur.id = w.currency_id " +
                (isNotEmpty(currencyNames) ? "AND cur.name IN (:currencies) " : StringUtils.EMPTY) +
                "WHERE w.user_id = :user_id " +
                (withoutZeroBalances ? "AND IFNULL(w.active_balance + w.reserved_balance, 0) > 0 " : StringUtils.EMPTY) +
                limitStr +
                offsetStr;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        if (isNotEmpty(currencyNames)) {
            params.put("currencies", String.join(",", currencyNames));
        }

        return coreNPTemplate.query(sql, params, getUserBalancesInfoDtoRowMapper());
    }

    @Override
    public void updateUserOperationAuthority(List<CoreUserOperationAuthorityOptionDto> options, Integer userId) {
        final String sql = "UPDATE USER_OPERATION_AUTHORITY uoa" +
                " SET uoa.enabled = ?" +
                " WHERE uoa.user_id = ? AND uoa.user_operation_id = ?";

        coreTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CoreUserOperationAuthorityOptionDto authorityOption = options.get(i);
                ps.setBoolean(1, authorityOption.getEnabled());
                ps.setInt(2, userId);
                ps.setInt(3, authorityOption.getUserOperationAuthority().getOperationId());
            }

            @Override
            public int getBatchSize() {
                return options.size();
            }
        });
    }

    @Override
    public List<CoreUserOperationAuthorityOptionDto> getUserOperationTypeAuthorities(Integer userId) {
        final String sql = "SELECT uoa.user_operation_id, uoa.enabled " +
                "FROM USER_OPERATION_AUTHORITY uoa " +
                "WHERE uoa.user_id = :user_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        return coreNPTemplate.query(sql, params, (rs, i) -> CoreUserOperationAuthorityOptionDto.builder()
                .userOperationAuthority(UserOperationAuthority.convert(rs.getInt("user_operation_id")))
                .enabled(rs.getBoolean("enabled"))
                .build());
    }

    @Override
    public void updateUserRole(UserRole newRole, Integer userId) {
        final String sql = "UPDATE USER u " +
                "SET u.roleid = :role_id " +
                "WHERE u.id = :user_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("role_id", newRole.getRole());

        boolean updated = coreNPTemplate.update(sql, params) > 0;
        if (!updated) {
            log.error("User role not updated (user id: {}, new role: {})", userId, newRole);
        }
    }

    public List<UserRole> getAllRoles() {
        final String sql = "SELECT ur.name FROM USER_ROLE ur";

        return coreNPTemplate.query(sql, (rs, row) -> UserRole.valueOf(rs.getString("name")));
    }

    @Override
    public List<ReferralTransactionDto> getUserReferralTransactionList(Integer userId) {
        final String sql = "SELECT " +
                "child.id AS initiator_id, " +
                "child.email AS initiator_email, " +
                "rl.level AS referral_level, " +
                "rl.percent AS referral_percent, " +
                "rt.order_id " +
                "FROM REFERRAL_TRANSACTION rt " +
                "JOIN USER child ON child.id = rt.initiator_id " +
                "JOIN REFERRAL_LEVEL rl ON rl.id = rt.referral_level_id " +
                "WHERE rt.user_id = :user_id AND rt.status = \'PAYED\'";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        return coreNPTemplate.query(sql, params, getUserReferralInfoDtoRowMapper());
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
                .lastLoginDate(rs.getTimestamp("last_login_date").toLocalDateTime())
                .phone(rs.getString("phone"))
                .verificationStatus(rs.getString("verification_status"))
                .role(UserRole.valueOf(rs.getString("role_name")))
                .status(UserStatus.convert(rs.getInt("status_id")))
                .build();
    }

    private RowMapper<UserBalancesInfoDto> getUserBalancesInfoDtoRowMapper() {
        return (rs, idx) -> UserBalancesInfoDto.builder()
                .currencyName(rs.getString("currency_name"))
                .lastRefillAddress(rs.getString("last_refill_address"))
                .summaryRefill(rs.getBigDecimal("all_refill_sum"))
                .lastWithdrawAddress(rs.getString("last_withdraw_address"))
                .summaryWithdraw(rs.getBigDecimal("all_withdraw_sum"))
                .activeBalance(rs.getBigDecimal("active_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .commonBalance(rs.getBigDecimal("common_balance"))
                .build();
    }

    private RowMapper<ReferralTransactionDto> getUserReferralInfoDtoRowMapper() {
        return (rs, idx) -> ReferralTransactionDto.builder()
                .initiatorId(rs.getInt("initiator_id"))
                .initiatorEmail(rs.getString("initiator_email"))
                .referralLevel(rs.getInt("referral_level"))
                .referralPercent(rs.getBigDecimal("referral_percent"))
                .orderId(rs.getInt("order_id"))
                .build();
    }
}