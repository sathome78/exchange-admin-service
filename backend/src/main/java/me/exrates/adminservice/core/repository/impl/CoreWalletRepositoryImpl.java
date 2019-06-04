package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class CoreWalletRepositoryImpl implements CoreWalletRepository {

    private final NamedParameterJdbcOperations coreTemplate;

    @Autowired
    public CoreWalletRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreTemplate) {
        this.coreTemplate = coreTemplate;
    }

    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        final String sql = "SELECT cur.id AS currency_id, " +
                "cur.name AS currency_name, " +
                "ur.id AS role_id, " +
                "ur.name AS role_name, " +
                "w.active_balance, " +
                "w.reserved_balance" +
                " FROM WALLET w" +
                " JOIN CURRENCY cur ON cur.id = w.currency_id AND cur.hidden = 0" +
                " JOIN USER u ON u.id = w.user_id" +
                " JOIN USER_ROLE ur ON ur.id = u.roleid" +
                " GROUP BY cur.id, ur.id" +
                " ORDER BY cur.id, ur.id";

        return coreTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .totalBalance(rs.getBigDecimal("active_balance").add(rs.getBigDecimal("reserved_balance")))
                .build());
    }
}