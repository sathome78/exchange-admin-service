package me.exrates.adminservice.core.repository.impl;

import com.google.common.collect.Maps;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import me.exrates.adminservice.domain.ClosedOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static me.exrates.adminservice.configurations.CoreDatasourceConfiguration.CORE_NP_TEMPLATE;

@Repository
public class CoreOrderRepositoryImpl implements CoreOrderRepository {

    private final NamedParameterJdbcOperations coreTemplate;

    @Autowired
    public CoreOrderRepositoryImpl(@Qualifier(CORE_NP_TEMPLATE) NamedParameterJdbcOperations ops) {
        this.coreTemplate = ops;
    }

    @Override
    public List<ClosedOrder> findAllLimited(int chunkSize, int maxId) {
        String sql = "SELECT E.id, CP.name, E.user_id, E.user_acceptor_id, E.exrate, E.amount_base, E.amount_convert," +
                " DATE(E.date_acception) as closed, E.base_type" +
                " FROM EXORDERS E" +
                " LEFT JOIN CURRENCY_PAIR CP on E.currency_pair_id = CP.id" +
                " WHERE E.status_id = 3 AND E.user_id <> E.user_acceptor_id" +
                " AND E.id > :position" +
                " ORDER BY E.id ASC" +
                " LIMIT :size";
        Map<String, Object> params = Maps.newHashMap();
        params.put("size", chunkSize);
        params.put("position", maxId);
        return coreTemplate.query(sql, params, getRowMapper());
    }

    private RowMapper<ClosedOrder> getRowMapper() {
        return (rs, i) -> ClosedOrder.builder()
                .id(rs.getInt("id"))
                .currencyPairName(rs.getString("name"))
                .creatorId(rs.getInt("user_id"))
                .acceptorId(rs.getInt("user_acceptor_id"))
                .rate(rs.getBigDecimal("exrate"))
                .amountBase(rs.getBigDecimal("amount_base"))
                .amountConvert(rs.getBigDecimal("amount_convert"))
                .closedDate(rs.getDate("closed").toLocalDate())
                .baseType(rs.getString("base_type"))
                .build();
    }
}
