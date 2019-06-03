package me.exrates.adminservice.core.repository.impl;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class CoreUserRepositoryImpl implements CoreUserRepository {

    private final NamedParameterJdbcOperations coreTemplate;

    @Autowired
    public CoreUserRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreTemplate) {
        this.coreTemplate = coreTemplate;
    }

    @Override
    public List<CoreUser> findAllAfterIdLimited(long lastUserId, int limit) {
        String sql = "SELECT u.id as user_id, pub_id, email, password, regdate, phone, UPPER(us.name) as user_status, " +
                "UPPER(ur.name) as user_role, use2fa, kyc_status FROM USER u " +
                "LEFT JOIN USER_STATUS us on u.status = us.id " +
                "LEFT JOIN USER_ROLE ur on u.roleid = ur.id " +
                "WHERE u.id > :lastId LIMIT :size;";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("lastId", lastUserId)
                .addValue("size", limit);
        return coreTemplate.query(sql, params, getCoreUserRowMapper());
    }

    private RowMapper<CoreUser> getCoreUserRowMapper() {
        return (rs, i) -> CoreUser.builder()
                .userId(rs.getInt(CoreUserRepository.COL_USER_ID))
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
